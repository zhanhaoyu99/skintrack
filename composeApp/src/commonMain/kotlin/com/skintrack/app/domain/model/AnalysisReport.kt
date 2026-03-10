package com.skintrack.app.domain.model

import kotlinx.datetime.Instant

data class AnalysisReport(
    val id: String,
    val userId: String,
    val skinRecordId: String,
    val summary: String,
    val recommendations: List<String>,
    val detailedJson: String? = null,
    val createdAt: Instant,
)
