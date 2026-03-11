package com.skintrack.app.data.remote

import com.skintrack.app.domain.model.SkinType

data class SkinAnalysisResult(
    val overallScore: Int,
    val acneCount: Int,
    val poreScore: Int,
    val rednessScore: Int,
    val evenScore: Int,
    val blackheadDensity: Int,
    val skinType: SkinType,
    val summary: String,
    val recommendations: List<String>,
)
