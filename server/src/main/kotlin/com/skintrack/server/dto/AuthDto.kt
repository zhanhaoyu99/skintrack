package com.skintrack.server.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class AuthResponse(
    @SerialName("user_id") val userId: String,
    val email: String,
    @SerialName("display_name") val displayName: String? = null,
    val token: String,
)
