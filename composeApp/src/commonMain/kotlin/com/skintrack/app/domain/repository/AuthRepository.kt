package com.skintrack.app.domain.repository

import com.skintrack.app.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observeAuthUser(): Flow<AuthUser?>
    suspend fun currentUser(): AuthUser?
    suspend fun login(email: String, password: String): Result<AuthUser>
    suspend fun register(email: String, password: String): Result<AuthUser>
    suspend fun logout()
    suspend fun requestPasswordReset(email: String): Result<Unit>
    suspend fun resetPassword(email: String, code: String, newPassword: String): Result<Unit>
    suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit>
    suspend fun refreshAccessToken(): Result<Unit>
    suspend fun deleteAccount(password: String): Result<Unit>
    suspend fun updateDisplayName(displayName: String): Result<Unit>
    suspend fun exportUserData(): Result<String>
}
