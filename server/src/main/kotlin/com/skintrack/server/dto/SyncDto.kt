package com.skintrack.server.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// These DTOs match the client-side SupabaseDto.kt exactly

@Serializable
data class SkinRecordDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("skin_type") val skinType: String,
    @SerialName("overall_score") val overallScore: Int? = null,
    @SerialName("acne_count") val acneCount: Int? = null,
    @SerialName("pore_score") val poreScore: Int? = null,
    @SerialName("redness_score") val rednessScore: Int? = null,
    @SerialName("even_score") val evenScore: Int? = null,
    @SerialName("blackhead_density") val blackheadDensity: Int? = null,
    val notes: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("analysis_json") val analysisJson: String? = null,
    @SerialName("recorded_at") val recordedAt: String,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class SkincareProductDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    val name: String,
    val brand: String? = null,
    val category: String,
    @SerialName("image_url") val imageUrl: String? = null,
    val barcode: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class DailyProductUsageDto(
    val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("product_id") val productId: String,
    @SerialName("used_date") val usedDate: String,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class UserSubscriptionDto(
    @SerialName("user_id") val userId: String,
    val plan: String,
    @SerialName("start_date") val startDate: String,
    @SerialName("expiry_date") val expiryDate: String,
    @SerialName("is_active") val isActive: Boolean,
)

@Serializable
data class CheckInStreakDto(
    @SerialName("user_id") val userId: String,
    @SerialName("current_streak") val currentStreak: Int,
    @SerialName("longest_streak") val longestStreak: Int,
    @SerialName("last_check_in_date") val lastCheckInDate: String? = null,
)

@Serializable
data class ImageUploadResponse(
    @SerialName("image_url") val imageUrl: String,
)
