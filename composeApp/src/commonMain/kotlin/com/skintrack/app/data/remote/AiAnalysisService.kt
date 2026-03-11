package com.skintrack.app.data.remote

import com.skintrack.app.domain.model.SkinType
import io.ktor.client.HttpClient
import kotlinx.coroutines.delay

class AiAnalysisService(
    private val httpClient: HttpClient,
) {
    // TODO: Replace mock with actual AI API call (GPT-4o / Gemini / Claude)
    suspend fun analyzeSkinImage(imageBytes: ByteArray): SkinAnalysisResult {
        delay(1500) // Simulate API latency

        val skinTypes = listOf(SkinType.OILY, SkinType.DRY, SkinType.COMBINATION, SkinType.NORMAL, SkinType.SENSITIVE)
        return SkinAnalysisResult(
            overallScore = (65..95).random(),
            acneCount = (0..8).random(),
            poreScore = (50..90).random(),
            rednessScore = (40..85).random(),
            evenScore = (55..90).random(),
            blackheadDensity = (10..60).random(),
            skinType = skinTypes.random(),
            summary = "皮肤状态整体良好，建议注意日常防晒和保湿。",
            recommendations = listOf(
                "坚持每日防晒，SPF30+",
                "使用温和洁面产品",
                "保持充足睡眠",
            ),
        )
    }
}
