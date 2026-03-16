package com.skintrack.app.data.repository

import com.skintrack.app.data.local.dao.AuthSessionDao
import com.skintrack.app.data.local.entity.AuthSessionEntity
import com.skintrack.app.domain.model.AuthUser
import com.skintrack.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class MockAuthRepository(
    private val authSessionDao: AuthSessionDao,
) : AuthRepository {

    override fun observeAuthUser(): Flow<AuthUser?> =
        authSessionDao.observeSession().map { it?.toDomain() }

    override suspend fun currentUser(): AuthUser? =
        authSessionDao.getSession()?.toDomain()

    override suspend fun login(email: String, password: String): Result<AuthUser> =
        authenticate(email, password)

    override suspend fun register(email: String, password: String): Result<AuthUser> =
        authenticate(email, password)

    override suspend fun logout() {
        authSessionDao.clearSession()
    }

    override suspend fun requestPasswordReset(email: String): Result<Unit> {
        // Mock: always succeed
        return Result.success(Unit)
    }

    override suspend fun resetPassword(email: String, code: String, newPassword: String): Result<Unit> {
        // Mock: accept code "123456"
        return if (code == "123456") {
            Result.success(Unit)
        } else {
            Result.failure(Exception("验证码无效或已过期"))
        }
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> {
        // Mock: always succeed
        return Result.success(Unit)
    }

    override suspend fun refreshAccessToken(): Result<Unit> {
        // Mock: always succeed
        return Result.success(Unit)
    }

    override suspend fun deleteAccount(password: String): Result<Unit> {
        // Mock: accept any non-empty password
        if (password.isBlank()) {
            return Result.failure(Exception("密码不能为空"))
        }
        authSessionDao.clearSession()
        return Result.success(Unit)
    }

    override suspend fun updateDisplayName(displayName: String): Result<Unit> {
        return try {
            authSessionDao.updateDisplayName(displayName)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception("更新昵称失败"))
        }
    }

    override suspend fun exportUserData(): Result<String> {
        // Mock: return sample JSON
        val sampleJson = """
            {
              "success": true,
              "data": {
                "profile": { "email": "test@example.com", "display_name": null, "created_at": "2026-01-01T00:00:00Z" },
                "skin_records": [],
                "products": [],
                "usage": [],
                "subscription": null
              }
            }
        """.trimIndent()
        return Result.success(sampleJson)
    }

    private suspend fun authenticate(email: String, password: String): Result<AuthUser> {
        if (!email.contains("@")) {
            return Result.failure(IllegalArgumentException("请输入有效的邮箱地址"))
        }
        if (password.isBlank()) {
            return Result.failure(IllegalArgumentException("密码不能为空"))
        }

        val session = AuthSessionEntity(
            userId = "local-user",
            email = email.trim(),
            createdAt = Clock.System.now().toEpochMilliseconds(),
        )
        authSessionDao.saveSession(session)

        return Result.success(session.toDomain())
    }

    private fun AuthSessionEntity.toDomain() = AuthUser(
        userId = userId,
        email = email,
        displayName = displayName,
    )
}
