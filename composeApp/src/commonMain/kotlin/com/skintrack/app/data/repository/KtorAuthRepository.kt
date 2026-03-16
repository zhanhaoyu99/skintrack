package com.skintrack.app.data.repository

import com.skintrack.app.data.local.dao.AuthSessionDao
import com.skintrack.app.data.local.entity.AuthSessionEntity
import com.skintrack.app.data.remote.dto.ApiResponse
import com.skintrack.app.data.remote.dto.AuthRequest
import com.skintrack.app.data.remote.dto.AuthResponse
import com.skintrack.app.data.remote.dto.DeleteAccountRequest
import com.skintrack.app.data.remote.dto.PasswordChangeRequest
import com.skintrack.app.data.remote.dto.PasswordResetRequest
import com.skintrack.app.data.remote.dto.PasswordResetVerifyRequest
import com.skintrack.app.data.remote.dto.ProfileUpdateRequest
import com.skintrack.app.data.remote.dto.RefreshTokenRequest
import com.skintrack.app.data.remote.dto.TokenResponse
import com.skintrack.app.domain.model.AuthUser
import com.skintrack.app.domain.repository.AuthRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
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

    override suspend fun requestPasswordReset(email: String): Result<Unit> {
        return try {
            val response: ApiResponse<String> = client.post("$baseUrl/api/auth/password/reset") {
                contentType(ContentType.Application.Json)
                setBody(PasswordResetRequest(email = email.trim()))
            }.body()

            if (!response.success) {
                return Result.failure(Exception(response.error ?: "未知错误"))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapAuthError(e.message)))
        }
    }

    override suspend fun resetPassword(email: String, code: String, newPassword: String): Result<Unit> {
        return try {
            val response: ApiResponse<String> = client.post("$baseUrl/api/auth/password/verify-reset") {
                contentType(ContentType.Application.Json)
                setBody(PasswordResetVerifyRequest(email = email.trim(), code = code, newPassword = newPassword))
            }.body()

            if (!response.success) {
                return Result.failure(Exception(response.error ?: "未知错误"))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapAuthError(e.message)))
        }
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> {
        return try {
            val response: ApiResponse<String> = client.post("$baseUrl/api/auth/password/change") {
                contentType(ContentType.Application.Json)
                setBody(PasswordChangeRequest(oldPassword = oldPassword, newPassword = newPassword))
            }.body()

            if (!response.success) {
                return Result.failure(Exception(response.error ?: "未知错误"))
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapAuthError(e.message)))
        }
    }

    override suspend fun refreshAccessToken(): Result<Unit> {
        return try {
            val session = authSessionDao.getSession()
                ?: return Result.failure(Exception("未登录"))
            val refreshToken = session.refreshToken
                ?: return Result.failure(Exception("无 refresh token"))

            val response: ApiResponse<TokenResponse> = client.post("$baseUrl/api/auth/token/refresh") {
                contentType(ContentType.Application.Json)
                setBody(RefreshTokenRequest(refreshToken = refreshToken))
            }.body()

            if (!response.success || response.data == null) {
                return Result.failure(Exception(response.error ?: "Token 刷新失败"))
            }

            val tokenData = response.data
            onTokenReceived(tokenData.token)

            authSessionDao.saveSession(
                session.copy(refreshToken = tokenData.refreshToken)
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapAuthError(e.message)))
        }
    }

    override suspend fun deleteAccount(password: String): Result<Unit> {
        return try {
            val response: ApiResponse<String> = client.delete("$baseUrl/api/auth/user") {
                contentType(ContentType.Application.Json)
                setBody(DeleteAccountRequest(password = password))
            }.body()

            if (!response.success) {
                return Result.failure(Exception(response.error ?: "未知错误"))
            }

            // Clear local session after successful server deletion
            onTokenReceived("")
            authSessionDao.clearSession()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapAuthError(e.message)))
        }
    }

    override suspend fun updateDisplayName(displayName: String): Result<Unit> {
        return try {
            // Update local session immediately
            authSessionDao.updateDisplayName(displayName)

            // Sync to server
            try {
                val response: ApiResponse<String> = client.put("$baseUrl/api/user/profile") {
                    contentType(ContentType.Application.Json)
                    setBody(ProfileUpdateRequest(displayName = displayName))
                }.body()

                if (!response.success) {
                    println("Warning: Failed to sync displayName to server: ${response.error}")
                }
            } catch (e: Exception) {
                println("Warning: Failed to sync displayName to server: ${e.message}")
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapAuthError(e.message)))
        }
    }

    override suspend fun exportUserData(): Result<String> {
        return try {
            val jsonText = client.get("$baseUrl/api/user/export").bodyAsText()
            Result.success(jsonText)
        } catch (e: Exception) {
            Result.failure(Exception(mapAuthError(e.message)))
        }
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
                refreshToken = auth.refreshToken,
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
        "密码至少" in message -> "密码至少需要8个字符"
        "邮箱格式" in message -> "请输入有效的邮箱地址"
        "验证码无效" in message -> "验证码无效或已过期"
        "旧密码错误" in message -> "旧密码错误"
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
