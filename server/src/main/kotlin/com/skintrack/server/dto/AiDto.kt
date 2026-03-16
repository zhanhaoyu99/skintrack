package com.skintrack.server.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// ── Skin Analysis ──────────────────────────────────────

@Serializable
data class SkinAnalysisRequest(
    @SerialName("image_base64") val imageBase64: String,
    @SerialName("skin_type") val skinType: String? = null,
)

@Serializable
data class SkinAnalysisResponse(
    @SerialName("overall_score") val overallScore: Int,
    @SerialName("acne_count") val acneCount: Int,
    @SerialName("pore_score") val poreScore: Int,
    @SerialName("redness_score") val rednessScore: Int,
    @SerialName("even_score") val evenScore: Int,
    @SerialName("blackhead_density") val blackheadDensity: Int,
    @SerialName("skin_type") val skinType: String,
    val summary: String,
    val recommendations: List<String>,
)

// ── Attribution Report ─────────────────────────────────

@Serializable
data class AttributionRequest(
    val records: List<AttributionSkinRecord>,
    val products: List<AttributionProduct>,
    val usages: List<AttributionUsage>,
)

@Serializable
data class AttributionSkinRecord(
    val date: String,
    @SerialName("overall_score") val overallScore: Int,
    @SerialName("acne_count") val acneCount: Int? = null,
    @SerialName("pore_score") val poreScore: Int? = null,
    @SerialName("redness_score") val rednessScore: Int? = null,
    @SerialName("even_score") val evenScore: Int? = null,
    @SerialName("blackhead_density") val blackheadDensity: Int? = null,
    @SerialName("skin_type") val skinType: String? = null,
)

@Serializable
data class AttributionProduct(
    val id: String,
    val name: String,
    val category: String,
    val brand: String? = null,
)

@Serializable
data class AttributionUsage(
    @SerialName("product_id") val productId: String,
    @SerialName("used_date") val usedDate: String,
)

@Serializable
data class AttributionReportResponse(
    val summary: String,
    @SerialName("overall_trend") val overallTrend: String,
    @SerialName("product_rankings") val productRankings: List<ProductAttributionDto>,
    val recommendations: List<String>,
)

@Serializable
data class ProductAttributionDto(
    @SerialName("product_name") val productName: String,
    val category: String,
    @SerialName("impact_score") val impactScore: Float,
    val explanation: String,
    @SerialName("days_used") val daysUsed: Int,
)
