package com.skintrack.app.domain.model

data class ProductAttribution(
    val product: SkincareProduct,
    val daysUsed: Int,
    val avgScoreWhenUsed: Float,
    val avgScoreWhenNotUsed: Float,
    val impact: Float, // avgUsed - avgNotUsed（正=有益，负=有害）
)
