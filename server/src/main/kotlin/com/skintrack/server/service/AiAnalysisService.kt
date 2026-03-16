package com.skintrack.server.service

import com.skintrack.server.config.AiConfig
import com.skintrack.server.dto.AttributionProduct
import com.skintrack.server.dto.AttributionReportResponse
import com.skintrack.server.dto.AttributionRequest
import com.skintrack.server.dto.AttributionSkinRecord
import com.skintrack.server.dto.AttributionUsage
import com.skintrack.server.dto.ProductAttributionDto
import com.skintrack.server.dto.SkinAnalysisResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import org.slf4j.LoggerFactory

class AiAnalysisService(
    private val config: AiConfig,
    private val httpClient: HttpClient,
) {
    private val logger = LoggerFactory.getLogger(AiAnalysisService::class.java)
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    // ── Skin Image Analysis ────────────────────────────────

    suspend fun analyzeSkin(imageBase64: String, skinType: String?): SkinAnalysisResponse {
        val prompt = buildSkinAnalysisPrompt(skinType)
        val responseText = callLlmWithRetry(prompt, imageBase64)
        return parseSkinAnalysisResponse(responseText)
    }

    private fun buildSkinAnalysisPrompt(skinType: String?): String {
        val skinTypeHint = if (!skinType.isNullOrBlank() && skinType != "UNKNOWN") {
            "\n用户自述肤质：$skinType"
        } else ""

        return """你是一位专业的皮肤科 AI 分析助手。请仔细分析这张面部照片的皮肤状况。$skinTypeHint

请从以下维度评估皮肤状态（0-100分，100为最佳）：
1. overall_score: 综合评分
2. acne_count: 可见痘痘/粉刺数量（整数，0-20+）
3. pore_score: 毛孔细腻度评分（100=毛孔不可见，0=毛孔非常粗大）
4. redness_score: 泛红程度评分（100=无泛红，0=严重泛红）
5. even_score: 肤色均匀度评分（100=完全均匀，0=极不均匀）
6. blackhead_density: 黑头密度（0-100，0=无黑头，100=大量黑头）
7. skin_type: 肤质判断（OILY/DRY/COMBINATION/SENSITIVE/NORMAL）
8. summary: 中文的皮肤状态总结（2-3句话）
9. recommendations: 3-5条中文护肤建议

请严格以 JSON 格式返回，不要包含任何其他文本：
{
  "overall_score": 75,
  "acne_count": 2,
  "pore_score": 70,
  "redness_score": 65,
  "even_score": 72,
  "blackhead_density": 20,
  "skin_type": "COMBINATION",
  "summary": "皮肤状态总结...",
  "recommendations": ["建议1", "建议2", "建议3"]
}"""
    }

    private fun parseSkinAnalysisResponse(responseText: String): SkinAnalysisResponse {
        val jsonStr = extractJson(responseText)
        val obj = json.parseToJsonElement(jsonStr).jsonObject
        return SkinAnalysisResponse(
            overallScore = obj.getInt("overall_score").coerceIn(0, 100),
            acneCount = obj.getInt("acne_count").coerceIn(0, 50),
            poreScore = obj.getInt("pore_score").coerceIn(0, 100),
            rednessScore = obj.getInt("redness_score").coerceIn(0, 100),
            evenScore = obj.getInt("even_score").coerceIn(0, 100),
            blackheadDensity = obj.getInt("blackhead_density").coerceIn(0, 100),
            skinType = obj.getString("skin_type", "UNKNOWN"),
            summary = obj.getString("summary", ""),
            recommendations = obj.getStringList("recommendations"),
        )
    }

    // ── Attribution Report ─────────────────────────────────

    suspend fun generateAttributionReport(request: AttributionRequest): AttributionReportResponse {
        val prompt = buildAttributionPrompt(request.records, request.usages, request.products)
        val responseText = callLlmWithRetry(prompt, imageBase64 = null)
        return parseAttributionResponse(responseText)
    }

    private fun buildAttributionPrompt(
        records: List<AttributionSkinRecord>,
        usages: List<AttributionUsage>,
        products: List<AttributionProduct>,
    ): String {
        val recordsJson = records.joinToString(",\n    ") { record ->
            buildString {
                append("{\"date\":\"${record.date}\"")
                append(",\"overall_score\":${record.overallScore}")
                record.acneCount?.let { append(",\"acne_count\":$it") }
                record.poreScore?.let { append(",\"pore_score\":$it") }
                record.rednessScore?.let { append(",\"redness_score\":$it") }
                record.evenScore?.let { append(",\"even_score\":$it") }
                record.blackheadDensity?.let { append(",\"blackhead_density\":$it") }
                record.skinType?.let { append(",\"skin_type\":\"$it\"") }
                append("}")
            }
        }

        val productMap = products.associateBy { it.id }
        val usagesByProduct = usages.groupBy { it.productId }
        val productsJson = usagesByProduct.entries.joinToString(",\n    ") { (productId, usageList) ->
            val product = productMap[productId]
            val name = product?.name ?: "Unknown"
            val category = product?.category ?: "Unknown"
            val dates = usageList.map { it.usedDate }.sorted()
            "{\"product_name\":\"$name\",\"category\":\"$category\",\"days_used\":${dates.size},\"usage_dates\":${dates.map { "\"$it\"" }}}"
        }

        return """你是一位专业的皮肤科医生和护肤品专家。根据以下用户的皮肤追踪数据和护肤品使用记录，分析哪些护肤品对皮肤改善最有效，哪些可能产生负面影响。

## 皮肤记录（时间序列）
[
    $recordsJson
]

## 护肤品使用记录
[
    $productsJson
]

请以 JSON 格式返回分析结果，不要包含任何其他文本：
{
  "summary": "简短的中文摘要，总结皮肤变化趋势和关键发现",
  "overall_trend": "improving/stable/declining",
  "product_rankings": [
    {
      "product_name": "产品名称",
      "category": "产品类别",
      "impact_score": 0.5,
      "explanation": "该产品对皮肤的影响分析",
      "days_used": 10
    }
  ],
  "recommendations": ["建议1", "建议2", "建议3"]
}

注意：
1. impact_score 范围为 -1.0 到 1.0，正值表示对皮肤有益，负值表示可能有害
2. 综合考虑产品使用天数、使用期间的皮肤指标变化
3. recommendations 请给出 3-5 条具体可行的建议
4. 所有文本使用中文"""
    }

    private fun parseAttributionResponse(responseText: String): AttributionReportResponse {
        val jsonStr = extractJson(responseText)
        val obj = json.parseToJsonElement(jsonStr).jsonObject
        val rankings = obj["product_rankings"]?.jsonArray?.map { element ->
            val rankObj = element.jsonObject
            ProductAttributionDto(
                productName = rankObj.getString("product_name", ""),
                category = rankObj.getString("category", ""),
                impactScore = rankObj["impact_score"]?.jsonPrimitive?.content?.toFloatOrNull()?.coerceIn(-1f, 1f) ?: 0f,
                explanation = rankObj.getString("explanation", ""),
                daysUsed = rankObj.getInt("days_used"),
            )
        } ?: emptyList()

        return AttributionReportResponse(
            summary = obj.getString("summary", ""),
            overallTrend = obj.getString("overall_trend", "stable"),
            productRankings = rankings,
            recommendations = obj.getStringList("recommendations"),
        )
    }

    // ── LLM API Call with Retry ────────────────────────────

    private suspend fun callLlmWithRetry(prompt: String, imageBase64: String?): String {
        val maxRetries = 3
        var delayMs = 1000L
        var lastException: Exception? = null

        for (attempt in 1..maxRetries) {
            try {
                return callLlm(prompt, imageBase64)
            } catch (e: Exception) {
                lastException = e
                logger.warn("LLM API call attempt $attempt/$maxRetries failed: ${e.message}")
                if (attempt < maxRetries) {
                    delay(delayMs)
                    delayMs *= 2
                }
            }
        }
        throw IllegalStateException("LLM API call failed after $maxRetries attempts", lastException)
    }

    private suspend fun callLlm(prompt: String, imageBase64: String?): String {
        return when (config.provider) {
            "openai" -> callOpenAi(prompt, imageBase64)
            "gemini" -> callGemini(prompt, imageBase64)
            "claude" -> callClaude(prompt, imageBase64)
            else -> throw IllegalArgumentException("Unsupported AI provider: ${config.provider}")
        }
    }

    // ── OpenAI GPT-4o ──────────────────────────────────────

    private suspend fun callOpenAi(prompt: String, imageBase64: String?): String {
        val requestBody = buildJsonObject {
            put("model", config.model)
            putJsonArray("messages") {
                add(buildJsonObject {
                    put("role", "system")
                    put("content", "You are a professional dermatology AI assistant. Always respond with valid JSON only.")
                })
                add(buildJsonObject {
                    put("role", "user")
                    if (imageBase64 != null) {
                        putJsonArray("content") {
                            add(buildJsonObject {
                                put("type", "text")
                                put("text", prompt)
                            })
                            add(buildJsonObject {
                                put("type", "image_url")
                                putJsonObject("image_url") {
                                    put("url", "data:image/jpeg;base64,$imageBase64")
                                    put("detail", "high")
                                }
                            })
                        }
                    } else {
                        put("content", prompt)
                    }
                })
            }
            put("max_tokens", 2000)
            put("temperature", 0.3)
            putJsonObject("response_format") {
                put("type", "json_object")
            }
        }

        val response = httpClient.post("https://api.openai.com/v1/chat/completions") {
            header("Authorization", "Bearer ${config.apiKey}")
            contentType(ContentType.Application.Json)
            setBody(requestBody.toString())
        }

        val body: String = response.body()
        val responseObj = json.parseToJsonElement(body).jsonObject
        return responseObj["choices"]!!.jsonArray[0].jsonObject["message"]!!
            .jsonObject["content"]!!.jsonPrimitive.content
    }

    // ── Google Gemini ──────────────────────────────────────

    private suspend fun callGemini(prompt: String, imageBase64: String?): String {
        val model = config.model
        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=${config.apiKey}"

        val requestBody = buildJsonObject {
            putJsonArray("contents") {
                add(buildJsonObject {
                    put("role", "user")
                    putJsonArray("parts") {
                        add(buildJsonObject { put("text", prompt) })
                        if (imageBase64 != null) {
                            add(buildJsonObject {
                                putJsonObject("inline_data") {
                                    put("mime_type", "image/jpeg")
                                    put("data", imageBase64)
                                }
                            })
                        }
                    }
                })
            }
            putJsonObject("generationConfig") {
                put("temperature", 0.3)
                put("maxOutputTokens", 2000)
                put("responseMimeType", "application/json")
            }
        }

        val response = httpClient.post(url) {
            contentType(ContentType.Application.Json)
            setBody(requestBody.toString())
        }

        val body: String = response.body()
        val responseObj = json.parseToJsonElement(body).jsonObject
        return responseObj["candidates"]!!.jsonArray[0].jsonObject["content"]!!
            .jsonObject["parts"]!!.jsonArray[0].jsonObject["text"]!!.jsonPrimitive.content
    }

    // ── Anthropic Claude ───────────────────────────────────

    private suspend fun callClaude(prompt: String, imageBase64: String?): String {
        val requestBody = buildJsonObject {
            put("model", config.model)
            put("max_tokens", 2000)
            put("temperature", 0.3)
            putJsonArray("messages") {
                add(buildJsonObject {
                    put("role", "user")
                    if (imageBase64 != null) {
                        putJsonArray("content") {
                            add(buildJsonObject {
                                put("type", "image")
                                putJsonObject("source") {
                                    put("type", "base64")
                                    put("media_type", "image/jpeg")
                                    put("data", imageBase64)
                                }
                            })
                            add(buildJsonObject {
                                put("type", "text")
                                put("text", prompt)
                            })
                        }
                    } else {
                        putJsonArray("content") {
                            add(buildJsonObject {
                                put("type", "text")
                                put("text", prompt)
                            })
                        }
                    }
                })
            }
        }

        val response = httpClient.post("https://api.anthropic.com/v1/messages") {
            header("x-api-key", config.apiKey)
            header("anthropic-version", "2023-06-01")
            contentType(ContentType.Application.Json)
            setBody(requestBody.toString())
        }

        val body: String = response.body()
        val responseObj = json.parseToJsonElement(body).jsonObject
        return responseObj["content"]!!.jsonArray[0].jsonObject["text"]!!.jsonPrimitive.content
    }

    // ── Rate Limiting ──────────────────────────────────────

    private val dailyUsage = java.util.concurrent.ConcurrentHashMap<String, java.util.concurrent.ConcurrentHashMap<String, java.util.concurrent.atomic.AtomicInteger>>()

    /**
     * Check rate limit and atomically increment usage count.
     * Returns true if within limit (and count is incremented), false if over limit.
     */
    fun checkAndRecordUsage(userId: String): Boolean {
        val today = java.time.LocalDate.now().toString()
        // Clear old dates
        dailyUsage.keys.filter { it != today }.forEach { dailyUsage.remove(it) }
        val todayUsage = dailyUsage.getOrPut(today) {
            java.util.concurrent.ConcurrentHashMap()
        }
        val counter = todayUsage.getOrPut(userId) {
            java.util.concurrent.atomic.AtomicInteger(0)
        }
        // Atomic check-and-increment
        while (true) {
            val current = counter.get()
            if (current >= config.maxDailyAnalysesPerUser) return false
            if (counter.compareAndSet(current, current + 1)) return true
        }
    }

    // ── JSON Helpers ───────────────────────────────────────

    private fun extractJson(text: String): String {
        // Handle cases where LLM wraps JSON in markdown code blocks
        val codeBlockRegex = Regex("```(?:json)?\\s*\\n?(\\{[\\s\\S]*?})\\s*\\n?```")
        val match = codeBlockRegex.find(text)
        if (match != null) return match.groupValues[1]

        // Try to find raw JSON object
        val jsonStart = text.indexOf('{')
        val jsonEnd = text.lastIndexOf('}')
        if (jsonStart >= 0 && jsonEnd > jsonStart) {
            return text.substring(jsonStart, jsonEnd + 1)
        }
        return text
    }

    private fun JsonObject.getInt(key: String): Int =
        this[key]?.jsonPrimitive?.content?.toDoubleOrNull()?.toInt() ?: 0

    private fun JsonObject.getString(key: String, default: String): String =
        this[key]?.jsonPrimitive?.content ?: default

    private fun JsonObject.getStringList(key: String): List<String> =
        this[key]?.jsonArray?.map { it.jsonPrimitive.content } ?: emptyList()
}
