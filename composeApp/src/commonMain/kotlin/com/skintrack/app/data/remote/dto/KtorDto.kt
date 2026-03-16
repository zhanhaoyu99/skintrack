package com.skintrack.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null,
)

@Serializable
data class AuthRequest(
    val email: String,
    val password: String,
)

@Serializable
data class AuthResponse(
    @SerialName("user_id") val userId: String,
    val email: String,
    @SerialName("display_name") val displayName: String? = null,
    val token: String,
    @SerialName("refresh_token") val refreshToken: String? = null,
)

@Serializable
data class TokenResponse(
    val token: String,
    @SerialName("refresh_token") val refreshToken: String,
)

@Serializable
data class PasswordResetRequest(
    val email: String,
)

@Serializable
data class PasswordResetVerifyRequest(
    val email: String,
    val code: String,
    @SerialName("new_password") val newPassword: String,
)

@Serializable
data class PasswordChangeRequest(
    @SerialName("old_password") val oldPassword: String,
    @SerialName("new_password") val newPassword: String,
)

@Serializable
data class RefreshTokenRequest(
    @SerialName("refresh_token") val refreshToken: String,
)

@Serializable
data class DeleteAccountRequest(
    val password: String,
)

@Serializable
data class ProfileUpdateRequest(
    @SerialName("display_name") val displayName: String? = null,
    @SerialName("skin_type") val skinType: String? = null,
)

@Serializable
data class ImageUploadResponse(
    @SerialName("image_url") val imageUrl: String,
)
