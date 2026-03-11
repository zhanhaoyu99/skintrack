package com.skintrack.app.domain.repository

import com.skintrack.app.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun observeAuthUser(): Flow<AuthUser?>
    suspend fun currentUser(): AuthUser?
    suspend fun login(email: String, password: String): Result<AuthUser>
    suspend fun register(email: String, password: String): Result<AuthUser>
    suspend fun logout()
}
