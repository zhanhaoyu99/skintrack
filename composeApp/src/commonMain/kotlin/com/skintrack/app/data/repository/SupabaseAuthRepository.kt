package com.skintrack.app.data.repository

import com.skintrack.app.data.local.dao.AuthSessionDao
import com.skintrack.app.data.local.entity.AuthSessionEntity
import com.skintrack.app.domain.model.AuthUser
import com.skintrack.app.domain.repository.AuthRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

class SupabaseAuthRepository(
    private val supabase: SupabaseClient,
    private val authSessionDao: AuthSessionDao,
) : AuthRepository {

    override fun observeAuthUser(): Flow<AuthUser?> =
        authSessionDao.observeSession().map { it?.toDomain() }

    override suspend fun currentUser(): AuthUser? =
        authSessionDao.getSession()?.toDomain()

    override suspend fun login(email: String, password: String): Result<AuthUser> {
        return try {
            supabase.auth.signInWith(Email) {
                this.email = email.trim()
                this.password = password
            }
            val user = supabase.auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("登录失败：无法获取用户信息"))

            val session = AuthSessionEntity(
                userId = user.id,
                email = user.email ?: email.trim(),
                displayName = user.userMetadata?.get("display_name")?.toString()?.trim('"'),
                createdAt = Clock.System.now().toEpochMilliseconds(),
            )
            authSessionDao.saveSession(session)

            Result.success(session.toDomain())
        } catch (e: Exception) {
            Result.failure(Exception(mapAuthError(e.message)))
        }
    }

    override suspend fun register(email: String, password: String): Result<AuthUser> {
        return try {
            supabase.auth.signUpWith(Email) {
                this.email = email.trim()
                this.password = password
            }
            val user = supabase.auth.currentUserOrNull()
                ?: return Result.failure(IllegalStateException("注册失败：无法获取用户信息"))

            val session = AuthSessionEntity(
                userId = user.id,
                email = user.email ?: email.trim(),
                displayName = user.userMetadata?.get("display_name")?.toString()?.trim('"'),
                createdAt = Clock.System.now().toEpochMilliseconds(),
            )
            authSessionDao.saveSession(session)

            Result.success(session.toDomain())
        } catch (e: Exception) {
            Result.failure(Exception(mapAuthError(e.message)))
        }
    }

    override suspend fun logout() {
        try {
            supabase.auth.signOut()
        } catch (_: Exception) {
            // Ignore network errors during logout
        }
        authSessionDao.clearSession()
    }

    override suspend fun requestPasswordReset(email: String): Result<Unit> {
        return try {
            supabase.auth.resetPasswordForEmail(email.trim())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapAuthError(e.message)))
        }
    }

    override suspend fun resetPassword(email: String, code: String, newPassword: String): Result<Unit> {
        // Supabase handles password reset via magic link, not code-based flow
        return Result.failure(Exception("Supabase 使用邮件链接重置密码，请查看邮箱"))
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> {
        return try {
            supabase.auth.updateUser { password = newPassword }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapAuthError(e.message)))
        }
    }

    override suspend fun refreshAccessToken(): Result<Unit> {
        return try {
            supabase.auth.refreshCurrentSession()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapAuthError(e.message)))
        }
    }

    override suspend fun deleteAccount(password: String): Result<Unit> {
        // Supabase account deletion requires admin API or edge function
        return Result.failure(Exception("Supabase 后端暂不支持自助注销，请联系客服"))
    }

    override suspend fun updateDisplayName(displayName: String): Result<Unit> {
        return try {
            supabase.auth.updateUser {
                data { put("display_name", kotlinx.serialization.json.JsonPrimitive(displayName)) }
            }
            authSessionDao.updateDisplayName(displayName)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(Exception(mapAuthError(e.message)))
        }
    }

    override suspend fun exportUserData(): Result<String> {
        // Supabase data export requires custom edge function
        return Result.failure(Exception("Supabase 后端暂不支持数据导出，请联系客服"))
    }

    private fun mapAuthError(message: String?): String = when {
        message == null -> "未知错误"
        "Invalid login credentials" in message -> "邮箱或密码错误"
        "Email not confirmed" in message -> "请先验证邮箱"
        "User already registered" in message -> "该邮箱已注册"
        "Password should be at least" in message -> "密码至少需要6个字符"
        "Unable to validate email" in message -> "请输入有效的邮箱地址"
        else -> message
    }

    private fun AuthSessionEntity.toDomain() = AuthUser(
        userId = userId,
        email = email,
        displayName = displayName,
    )
}
