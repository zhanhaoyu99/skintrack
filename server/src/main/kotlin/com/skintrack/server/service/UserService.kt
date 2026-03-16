package com.skintrack.server.service

import com.skintrack.server.auth.JwtService
import com.skintrack.server.auth.PasswordHash
import com.skintrack.server.database.tables.CheckInStreaksTable
import com.skintrack.server.database.tables.UserSubscriptionsTable
import com.skintrack.server.database.tables.UsersTable
import com.skintrack.server.dto.AuthResponse
import com.skintrack.server.dto.TokenResponse
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import com.skintrack.server.database.tables.DailyProductUsageTable
import com.skintrack.server.database.tables.SkinRecordsTable
import com.skintrack.server.database.tables.SkincareProductsTable
import com.skintrack.server.dto.DailyProductUsageDto
import com.skintrack.server.dto.SkinRecordDto
import com.skintrack.server.dto.SkincareProductDto
import com.skintrack.server.dto.UserExportData
import com.skintrack.server.dto.UserProfileExport
import com.skintrack.server.dto.UserSubscriptionDto
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.slf4j.LoggerFactory
import java.util.UUID
import kotlin.random.Random

class UserService(private val jwtService: JwtService) {

    private val logger = LoggerFactory.getLogger(UserService::class.java)

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        private val PASSWORD_UPPER = Regex("[A-Z]")
        private val PASSWORD_LOWER = Regex("[a-z]")
        private val PASSWORD_DIGIT = Regex("[0-9]")
        private const val RESET_CODE_EXPIRY_MS = 10 * 60 * 1000L // 10 minutes
    }

    fun register(email: String, password: String): AuthResponse {
        val trimmedEmail = email.trim().lowercase()

        if (!EMAIL_REGEX.matches(trimmedEmail)) {
            throw IllegalArgumentException("邮箱格式不正确")
        }

        validatePassword(password)

        val existing = transaction {
            UsersTable.selectAll().where { UsersTable.email eq trimmedEmail }.firstOrNull()
        }
        if (existing != null) throw IllegalStateException("该邮箱已注册")

        val userId = UUID.randomUUID().toString()
        val now = Clock.System.now().toString()
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

        val token = jwtService.generateToken(userId, trimmedEmail)
        val refreshToken = jwtService.generateRefreshToken(userId, trimmedEmail)

        transaction {
            UsersTable.insert {
                it[id] = userId
                it[UsersTable.email] = trimmedEmail
                it[passwordHash] = PasswordHash.hash(password)
                it[UsersTable.refreshToken] = refreshToken
                it[createdAt] = now
            }

            // Create 14-day trial subscription
            UserSubscriptionsTable.insert {
                it[UserSubscriptionsTable.userId] = userId
                it[plan] = "TRIAL"
                it[startDate] = today.toString()
                it[expiryDate] = today.plus(DatePeriod(days = 14)).toString()
                it[isActive] = true
            }

            // Init check-in streak
            CheckInStreaksTable.insert {
                it[CheckInStreaksTable.userId] = userId
                it[currentStreak] = 0
                it[longestStreak] = 0
            }
        }

        return AuthResponse(
            userId = userId,
            email = trimmedEmail,
            token = token,
            refreshToken = refreshToken,
        )
    }

    fun login(email: String, password: String): AuthResponse {
        val trimmedEmail = email.trim().lowercase()

        val user = transaction {
            UsersTable.selectAll().where { UsersTable.email eq trimmedEmail }.firstOrNull()
        } ?: throw IllegalStateException("邮箱或密码错误")

        val hash = user[UsersTable.passwordHash]
        if (!PasswordHash.verify(password, hash)) {
            throw IllegalStateException("邮箱或密码错误")
        }

        val userId = user[UsersTable.id]
        val displayName = user[UsersTable.displayName]
        val token = jwtService.generateToken(userId, trimmedEmail)
        val refreshToken = jwtService.generateRefreshToken(userId, trimmedEmail)

        // Store refresh token
        transaction {
            UsersTable.update({ UsersTable.id eq userId }) {
                it[UsersTable.refreshToken] = refreshToken
            }
        }

        return AuthResponse(
            userId = userId,
            email = trimmedEmail,
            displayName = displayName,
            token = token,
            refreshToken = refreshToken,
        )
    }

    fun refreshToken(refreshTokenValue: String): TokenResponse {
        // Verify the refresh token
        val verifier = com.auth0.jwt.JWT.require(jwtService.algorithm)
            .withIssuer(jwtService.issuer)
            .withAudience(jwtService.audience)
            .build()

        val decoded = try {
            verifier.verify(refreshTokenValue)
        } catch (e: Exception) {
            throw IllegalArgumentException("Refresh token 无效或已过期")
        }

        val tokenType = decoded.getClaim("type").asString()
        if (tokenType != "refresh") {
            throw IllegalArgumentException("无效的 token 类型")
        }

        val userId = decoded.subject
        val email = decoded.getClaim("email").asString()

        // Verify the token matches what's stored in the database
        val user = transaction {
            UsersTable.selectAll().where { UsersTable.id eq userId }.firstOrNull()
        } ?: throw IllegalArgumentException("用户不存在")

        val storedRefreshToken = user[UsersTable.refreshToken]
        if (storedRefreshToken != refreshTokenValue) {
            throw IllegalArgumentException("Refresh token 已失效")
        }

        // Generate new tokens
        val newToken = jwtService.generateToken(userId, email)
        val newRefreshToken = jwtService.generateRefreshToken(userId, email)

        // Update stored refresh token
        transaction {
            UsersTable.update({ UsersTable.id eq userId }) {
                it[UsersTable.refreshToken] = newRefreshToken
            }
        }

        return TokenResponse(token = newToken, refreshToken = newRefreshToken)
    }

    fun requestPasswordReset(email: String) {
        val trimmedEmail = email.trim().lowercase()
        val code = Random.nextInt(100_000, 999_999).toString()
        val expiresAt = Clock.System.now().toEpochMilliseconds() + RESET_CODE_EXPIRY_MS

        val user = transaction {
            UsersTable.selectAll().where { UsersTable.email eq trimmedEmail }.firstOrNull()
        }

        if (user != null) {
            transaction {
                UsersTable.update({ UsersTable.email eq trimmedEmail }) {
                    it[resetCode] = code
                    it[resetCodeExpiresAt] = expiresAt
                }
            }
            // TODO: Send email with verification code
            logger.info("Password reset requested for $trimmedEmail")
        }
        // Always return success to prevent email enumeration
    }

    fun verifyResetCode(email: String, code: String): Boolean {
        val trimmedEmail = email.trim().lowercase()
        val user = transaction {
            UsersTable.selectAll().where { UsersTable.email eq trimmedEmail }.firstOrNull()
        } ?: return false

        val storedCode = user[UsersTable.resetCode] ?: return false
        val expiresAt = user[UsersTable.resetCodeExpiresAt] ?: return false
        val now = Clock.System.now().toEpochMilliseconds()

        return java.security.MessageDigest.isEqual(
            storedCode.toByteArray(), code.toByteArray()
        ) && now <= expiresAt
    }

    fun resetPassword(email: String, code: String, newPassword: String) {
        val trimmedEmail = email.trim().lowercase()

        if (!verifyResetCode(trimmedEmail, code)) {
            throw IllegalArgumentException("验证码无效或已过期")
        }

        validatePassword(newPassword)

        transaction {
            UsersTable.update({ UsersTable.email eq trimmedEmail }) {
                it[passwordHash] = PasswordHash.hash(newPassword)
                it[resetCode] = null
                it[resetCodeExpiresAt] = null
            }
        }
    }

    fun changePassword(userId: String, oldPassword: String, newPassword: String) {
        val user = transaction {
            UsersTable.selectAll().where { UsersTable.id eq userId }.firstOrNull()
        } ?: throw IllegalStateException("用户不存在")

        if (!PasswordHash.verify(oldPassword, user[UsersTable.passwordHash])) {
            throw IllegalStateException("旧密码错误")
        }

        validatePassword(newPassword)

        transaction {
            UsersTable.update({ UsersTable.id eq userId }) {
                it[passwordHash] = PasswordHash.hash(newPassword)
            }
        }
    }

    fun deleteAccount(userId: String, password: String) {
        val user = transaction {
            UsersTable.selectAll().where { UsersTable.id eq userId }.firstOrNull()
        } ?: throw IllegalStateException("用户不存在")

        if (!PasswordHash.verify(password, user[UsersTable.passwordHash])) {
            throw IllegalStateException("密码错误")
        }

        transaction {
            // Delete in order respecting foreign key constraints
            DailyProductUsageTable.deleteWhere { DailyProductUsageTable.userId eq userId }
            SkinRecordsTable.deleteWhere { SkinRecordsTable.userId eq userId }
            SkincareProductsTable.deleteWhere { SkincareProductsTable.userId eq userId }
            UserSubscriptionsTable.deleteWhere { UserSubscriptionsTable.userId eq userId }
            CheckInStreaksTable.deleteWhere { CheckInStreaksTable.userId eq userId }
            UsersTable.deleteWhere { UsersTable.id eq userId }
        }

        logger.info("Account deleted for user $userId")
    }

    fun exportUserData(userId: String): UserExportData {
        val user = transaction {
            UsersTable.selectAll().where { UsersTable.id eq userId }.firstOrNull()
        } ?: throw IllegalStateException("用户不存在")

        return transaction {
            val profile = UserProfileExport(
                email = user[UsersTable.email],
                displayName = user[UsersTable.displayName],
                createdAt = user[UsersTable.createdAt],
            )

            val skinRecords = SkinRecordsTable.selectAll()
                .where { SkinRecordsTable.userId eq userId }
                .map { row ->
                    SkinRecordDto(
                        id = row[SkinRecordsTable.id],
                        userId = row[SkinRecordsTable.userId],
                        skinType = row[SkinRecordsTable.skinType],
                        overallScore = row[SkinRecordsTable.overallScore],
                        acneCount = row[SkinRecordsTable.acneCount],
                        poreScore = row[SkinRecordsTable.poreScore],
                        rednessScore = row[SkinRecordsTable.rednessScore],
                        evenScore = row[SkinRecordsTable.evenScore],
                        blackheadDensity = row[SkinRecordsTable.blackheadDensity],
                        notes = row[SkinRecordsTable.notes],
                        imageUrl = row[SkinRecordsTable.imageUrl],
                        analysisJson = row[SkinRecordsTable.analysisJson],
                        recordedAt = row[SkinRecordsTable.recordedAt],
                        createdAt = row[SkinRecordsTable.createdAt],
                        updatedAt = row[SkinRecordsTable.updatedAt],
                    )
                }

            val products = SkincareProductsTable.selectAll()
                .where { SkincareProductsTable.userId eq userId }
                .map { row ->
                    SkincareProductDto(
                        id = row[SkincareProductsTable.id],
                        userId = row[SkincareProductsTable.userId],
                        name = row[SkincareProductsTable.name],
                        brand = row[SkincareProductsTable.brand],
                        category = row[SkincareProductsTable.category],
                        imageUrl = row[SkincareProductsTable.imageUrl],
                        barcode = row[SkincareProductsTable.barcode],
                        createdAt = row[SkincareProductsTable.createdAt],
                        updatedAt = row[SkincareProductsTable.updatedAt],
                    )
                }

            val usage = DailyProductUsageTable.selectAll()
                .where { DailyProductUsageTable.userId eq userId }
                .map { row ->
                    DailyProductUsageDto(
                        id = row[DailyProductUsageTable.id],
                        userId = row[DailyProductUsageTable.userId],
                        productId = row[DailyProductUsageTable.productId],
                        usedDate = row[DailyProductUsageTable.usedDate],
                        createdAt = row[DailyProductUsageTable.createdAt],
                    )
                }

            val subscription = UserSubscriptionsTable.selectAll()
                .where { UserSubscriptionsTable.userId eq userId }
                .firstOrNull()?.let { row ->
                    UserSubscriptionDto(
                        userId = row[UserSubscriptionsTable.userId],
                        plan = row[UserSubscriptionsTable.plan],
                        startDate = row[UserSubscriptionsTable.startDate],
                        expiryDate = row[UserSubscriptionsTable.expiryDate],
                        isActive = row[UserSubscriptionsTable.isActive],
                    )
                }

            UserExportData(
                profile = profile,
                skinRecords = skinRecords,
                products = products,
                usage = usage,
                subscription = subscription,
            )
        }
    }

    fun updateProfile(userId: String, displayName: String?, skinType: String?): Result<Unit> {
        return try {
            val user = transaction {
                UsersTable.selectAll().where { UsersTable.id eq userId }.firstOrNull()
            } ?: return Result.failure(IllegalStateException("用户不存在"))

            transaction {
                UsersTable.update({ UsersTable.id eq userId }) {
                    if (displayName != null) it[UsersTable.displayName] = displayName
                    if (skinType != null) it[UsersTable.skinType] = skinType
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            logger.error("Failed to update profile for user $userId", e)
            Result.failure(e)
        }
    }

    private fun validatePassword(password: String) {
        if (password.length < 8) {
            throw IllegalArgumentException("密码至少需要8个字符")
        }
        if (!PASSWORD_UPPER.containsMatchIn(password)) {
            throw IllegalArgumentException("密码必须包含至少一个大写字母")
        }
        if (!PASSWORD_LOWER.containsMatchIn(password)) {
            throw IllegalArgumentException("密码必须包含至少一个小写字母")
        }
        if (!PASSWORD_DIGIT.containsMatchIn(password)) {
            throw IllegalArgumentException("密码必须包含至少一个数字")
        }
    }
}
