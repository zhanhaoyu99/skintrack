package com.skintrack.app.domain.model

data class AttributionReport(
    val summary: String, // AI generated summary
    val overallTrend: String, // "improving" / "stable" / "declining"
    val productRankings: List<AiProductAttribution>, // Product impact rankings
    val recommendations: List<String>, // AI suggestions
    val generatedAt: Long,
)

data class AiProductAttribution(
    val productName: String,
    val category: String,
    val impactScore: Float, // -1.0 ~ 1.0
    val explanation: String, // AI explanation
    val daysUsed: Int,
)
