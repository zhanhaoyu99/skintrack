package com.skintrack.app.domain.model

import kotlinx.datetime.Instant

data class SkinRecord(
    val id: String,
    val userId: String,
    val skinType: SkinType,
    val overallScore: Int? = null,
    val acneCount: Int? = null,
    val poreScore: Int? = null,
    val rednessScore: Int? = null,
    val evenScore: Int? = null,
    val blackheadDensity: Int? = null,
    val notes: String? = null,
    val imageUrl: String? = null,
    val localImagePath: String? = null,
    val analysisJson: String? = null,
    val recordedAt: Instant,
    val createdAt: Instant,
    val synced: Boolean = false,
)

enum class SkinType {
    OILY, DRY, COMBINATION, SENSITIVE, NORMAL, UNKNOWN
}
