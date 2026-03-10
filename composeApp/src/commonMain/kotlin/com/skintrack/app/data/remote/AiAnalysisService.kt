package com.skintrack.app.data.remote

import com.skintrack.app.domain.model.AnalysisReport
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

class AiAnalysisService(
    private val httpClient: HttpClient,
) {
    // TODO: Implement actual AI API call (GPT-4o / Gemini / Claude)
    suspend fun analyzeSkinImage(
        imageBytes: ByteArray,
        userId: String,
        recordId: String,
    ): AnalysisReport {
        // Placeholder — will call multi-modal LLM API
        return AnalysisReport(
            id = "",
            userId = userId,
            skinRecordId = recordId,
            summary = "AI分析功能即将上线",
            recommendations = emptyList(),
            detailedJson = null,
            createdAt = Clock.System.now(),
        )
    }
}
