package com.skintrack.server.service

import com.skintrack.server.auth.JwtService
import com.skintrack.server.auth.PasswordHash
import com.skintrack.server.database.tables.CheckInStreaksTable
import com.skintrack.server.database.tables.UserSubscriptionsTable
import com.skintrack.server.database.tables.UsersTable
import com.skintrack.server.dto.AuthResponse
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class UserService(private val jwtService: JwtService) {

    fun register(email: String, password: String): AuthResponse {
        val trimmedEmail = email.trim().lowercase()

        val existing = transaction {
            UsersTable.selectAll().where { UsersTable.email eq trimmedEmail }.firstOrNull()
        }
        if (existing != null) throw IllegalStateException("该邮箱已注册")

        if (password.length < 6) throw IllegalArgumentException("密码至少需要6个字符")

        val userId = UUID.randomUUID().toString()
        val now = Clock.System.now().toString()
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

        transaction {
            UsersTable.insert {
                it[id] = userId
                it[UsersTable.email] = trimmedEmail
                it[passwordHash] = PasswordHash.hash(password)
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

        val token = jwtService.generateToken(userId, trimmedEmail)
        return AuthResponse(userId = userId, email = trimmedEmail, token = token)
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

        return AuthResponse(
            userId = userId,
            email = trimmedEmail,
            displayName = displayName,
            token = token,
        )
    }
}
