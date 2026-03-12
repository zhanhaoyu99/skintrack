package com.skintrack.app.data.repository

import com.skintrack.app.data.local.dao.AuthSessionDao
import com.skintrack.app.data.local.entity.AuthSessionEntity
import com.skintrack.app.data.remote.dto.ApiResponse
import com.skintrack.app.data.remote.dto.AuthRequest
import com.skintrack.app.data.remote.dto.AuthResponse
import com.skintrack.app.domain.model.AuthUser
import com.skintrack.app.domain.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class KtorAuthRepository(
    private val client: HttpClient,
    private val baseUrl: String,
    private val authSessionDao: AuthSessionDao,
    private val onTokenReceived: (String) -> Unit,
) : AuthRepository {

    override fun observeAuthUser(): Flow<AuthUser?> =
        authSessionDao.observeSession().map { it?.toDomain() }

    override suspend fun currentUser(): AuthUser? =
        authSessionDao.getSession()?.toDomain()

    override suspend fun login(email: String, password: String): Result<AuthUser> =
        authenticate("$baseUrl/api/auth/login", email, password)

    override suspend fun register(email: String, password: String): Result<AuthUser> =
        authenticate("$baseUrl/api/auth/register", email, password)

    override suspend fun logout() {
        onTokenReceived("")
        authSessionDao.clearSession()
    }

    private suspend fun authenticate(url: String, email: String, password: String): Result<AuthUser> {
        return try {
            val response: ApiResponse<AuthResponse> = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(AuthRequest(email = email.trim(), password = password))
            }.body()

            if (!response.success || response.data == null) {
                return Result.failure(Exception(response.error ?: "未知错误"))
            }

            val auth = response.data
            onTokenReceived(auth.token)

            val session = AuthSessionEntity(
                userId = auth.userId,
                email = auth.email,
                displayName = auth.displayName,
                createdAt = Clock.System.now().toEpochMilliseconds(),
            )
            authSessionDao.saveSession(session)

            Result.success(session.toDomain())
        } catch (e: Exception) {
            Result.failure(Exception(mapAuthError(e.message)))
        }
    }

    private fun mapAuthError(message: String?): String = when {
        message == null -> "未知错误"
        "邮箱或密码错误" in message -> "邮箱或密码错误"
        "该邮箱已注册" in message -> "该邮箱已注册"
        "密码至少" in message -> "密码至少需要6个字符"
        "邮箱格式" in message -> "请输入有效的邮箱地址"
        "Connection refused" in message -> "无法连接到服务器"
        "timeout" in message.lowercase() -> "连接超时，请稍后重试"
        else -> message
    }

    private fun AuthSessionEntity.toDomain() = AuthUser(
        userId = userId,
        email = email,
        displayName = displayName,
    )
}
