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
)

@Serializable
data class ImageUploadResponse(
    @SerialName("image_url") val imageUrl: String,
)
