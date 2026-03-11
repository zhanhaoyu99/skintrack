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
