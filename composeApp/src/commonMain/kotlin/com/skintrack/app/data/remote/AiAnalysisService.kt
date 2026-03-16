package com.skintrack.app.data.remote

import com.skintrack.app.data.remote.dto.ApiResponse
import com.skintrack.app.data.remote.dto.AttributionProductDto
import com.skintrack.app.data.remote.dto.AttributionReportResponseDto
import com.skintrack.app.data.remote.dto.AttributionRequestDto
import com.skintrack.app.data.remote.dto.AttributionSkinRecordDto
import com.skintrack.app.data.remote.dto.AttributionUsageDto
import com.skintrack.app.data.remote.dto.SkinAnalysisRequest
import com.skintrack.app.data.remote.dto.SkinAnalysisResponse
import com.skintrack.app.domain.model.AiProductAttribution
import com.skintrack.app.domain.model.AttributionReport
import com.skintrack.app.domain.model.DailyProductUsage
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.domain.model.SkinType
import com.skintrack.app.domain.model.SkincareProduct
import com.skintrack.app.domain.model.displayName
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.roundToInt
import kotlin.random.Random

class AiAnalysisService(
    private val httpClient: HttpClient,
) {
    /**
     * Analyze skin image via server-side LLM API.
     * Falls back to mock data when server is not configured.
     */
    @OptIn(ExperimentalEncodingApi::class)
    suspend fun analyzeSkinImage(imageBytes: ByteArray): SkinAnalysisResult {
        if (!KtorServerConfig.isConfigured) return mockAnalyze()

        return try {
            val base64 = Base64.encode(imageBytes)
            val response = httpClient.post("${KtorServerConfig.baseUrl}/api/ai/analyze-skin") {
                contentType(ContentType.Application.Json)
                setBody(SkinAnalysisRequest(imageBase64 = base64))
            }
            val apiResponse: ApiResponse<SkinAnalysisResponse> = response.body()
            val data = apiResponse.data ?: throw IllegalStateException(apiResponse.error ?: "Analysis failed")
            data.toDomain()
        } catch (e: Exception) {
            // Fallback to mock when server call fails
            println("Server AI analysis failed, falling back to mock: ${e.message}")
            mockAnalyze()
        }
    }

    /**
     * Generate an AI-powered attribution report analyzing how skincare products
     * correlate with skin condition changes over time.
     *
     * @param records Recent skin records with scores (time series)
     * @param usages Product usage records for the same period
     * @param products All user's skincare products (for name/category lookup)
     * @return AttributionReport with AI-generated insights
     */
    suspend fun generateAttributionReport(
        records: List<SkinRecord>,
        usages: List<DailyProductUsage>,
        products: Map<String, SkincareProduct>,
    ): AttributionReport {
        if (!KtorServerConfig.isConfigured) {
            return mockAttributionAnalysis(records, usages, products)
        }

        return try {
            val request = buildAttributionRequest(records, usages, products)
            val response = httpClient.post("${KtorServerConfig.baseUrl}/api/ai/attribution-report") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            val apiResponse: ApiResponse<AttributionReportResponseDto> = response.body()
            val data = apiResponse.data ?: throw IllegalStateException(apiResponse.error ?: "Attribution analysis failed")
            data.toDomain()
        } catch (e: Exception) {
            println("Server attribution analysis failed, falling back to mock: ${e.message}")
            mockAttributionAnalysis(records, usages, products)
        }
    }

    // ── Server request builders ────────────────────────────

    private fun buildAttributionRequest(
        records: List<SkinRecord>,
        usages: List<DailyProductUsage>,
        products: Map<String, SkincareProduct>,
    ): AttributionRequestDto {
        val tz = TimeZone.currentSystemDefault()
        return AttributionRequestDto(
            records = records.mapNotNull { record ->
                val score = record.overallScore ?: return@mapNotNull null
                val date = record.recordedAt.toLocalDateTime(tz).date
                AttributionSkinRecordDto(
                    date = date.toString(),
                    overallScore = score,
                    acneCount = record.acneCount,
                    poreScore = record.poreScore,
                    rednessScore = record.rednessScore,
                    evenScore = record.evenScore,
                    blackheadDensity = record.blackheadDensity,
                    skinType = record.skinType.name,
                )
            },
            products = products.values.map { product ->
                AttributionProductDto(
                    id = product.id,
                    name = product.name,
                    category = product.category.displayName,
                    brand = product.brand,
                )
            },
            usages = usages.map { usage ->
                AttributionUsageDto(
                    productId = usage.productId,
                    usedDate = usage.usedDate.toString(),
                )
            },
        )
    }

    // ── DTO to domain mappers ──────────────────────────────

    private fun SkinAnalysisResponse.toDomain(): SkinAnalysisResult {
        val parsedSkinType = try {
            SkinType.valueOf(skinType.uppercase())
        } catch (_: Exception) {
            SkinType.UNKNOWN
        }
        return SkinAnalysisResult(
            overallScore = overallScore,
            acneCount = acneCount,
            poreScore = poreScore,
            rednessScore = rednessScore,
            evenScore = evenScore,
            blackheadDensity = blackheadDensity,
            skinType = parsedSkinType,
            summary = summary,
            recommendations = recommendations,
        )
    }

    private fun AttributionReportResponseDto.toDomain(): AttributionReport {
        return AttributionReport(
            summary = summary,
            overallTrend = overallTrend,
            productRankings = productRankings.map { ranking ->
                AiProductAttribution(
                    productName = ranking.productName,
                    category = ranking.category,
                    impactScore = ranking.impactScore,
                    explanation = ranking.explanation,
                    daysUsed = ranking.daysUsed,
                )
            },
            recommendations = recommendations,
            generatedAt = Clock.System.now().toEpochMilliseconds(),
        )
    }

    // ── Mock attribution analysis (fallback) ───────────────

    private suspend fun mockAttributionAnalysis(
        records: List<SkinRecord>,
        usages: List<DailyProductUsage>,
        products: Map<String, SkincareProduct>,
    ): AttributionReport {
        delay(2000) // Simulate API latency

        val firstScore = records.firstOrNull()?.overallScore ?: 70
        val lastScore = records.lastOrNull()?.overallScore ?: 70
        val scoreDelta = lastScore - firstScore

        val trend = when {
            scoreDelta > 2 -> "improving"
            scoreDelta < -2 -> "declining"
            else -> "stable"
        }

        val trendDesc = when (trend) {
            "improving" -> "持续改善"
            "declining" -> "有所下降"
            else -> "基本稳定"
        }

        val productUsageCounts = usages.groupBy { it.productId }
            .mapValues { it.value.size }

        val rankings = productUsageCounts.mapNotNull { (productId, daysUsed) ->
            val product = products[productId] ?: return@mapNotNull null
            val baseImpact = when (product.category) {
                com.skintrack.app.domain.model.ProductCategory.SUNSCREEN -> 0.6f
                com.skintrack.app.domain.model.ProductCategory.SERUM -> 0.4f
                com.skintrack.app.domain.model.ProductCategory.CREAM -> 0.3f
                com.skintrack.app.domain.model.ProductCategory.CLEANSER -> 0.2f
                com.skintrack.app.domain.model.ProductCategory.TONER -> 0.1f
                com.skintrack.app.domain.model.ProductCategory.MASK -> 0.2f
                com.skintrack.app.domain.model.ProductCategory.EYE_CREAM -> 0.1f
                com.skintrack.app.domain.model.ProductCategory.EMULSION -> 0.15f
                com.skintrack.app.domain.model.ProductCategory.EXFOLIATOR -> -0.1f
                com.skintrack.app.domain.model.ProductCategory.OTHER -> 0.0f
            }
            val noise = Random.nextFloat() * 0.3f - 0.15f
            val impact = (baseImpact + noise).coerceIn(-1f, 1f)
            val roundedImpact = (impact * 100).roundToInt() / 100f

            val explanation = when {
                roundedImpact > 0.3f -> "使用期间皮肤指标明显改善，该产品对皮肤有积极影响"
                roundedImpact > 0.0f -> "使用期间皮肤状态有轻微改善，建议继续观察"
                roundedImpact > -0.2f -> "对皮肤的影响不明显，可能需要更长时间验证"
                else -> "使用期间皮肤指标有所下降，建议暂停使用并观察变化"
            }

            AiProductAttribution(
                productName = product.name,
                category = product.category.displayName,
                impactScore = roundedImpact,
                explanation = explanation,
                daysUsed = daysUsed,
            )
        }.sortedByDescending { it.impactScore }

        val topProduct = rankings.firstOrNull()
        val worstProduct = rankings.lastOrNull()?.takeIf { it.impactScore < 0 }

        val summary = buildString {
            append("在分析的 ${records.size} 条皮肤记录中，您的皮肤状态$trendDesc。")
            if (topProduct != null) {
                append("${topProduct.productName}（${topProduct.category}）表现最佳，使用期间皮肤评分有显著提升。")
            }
            if (worstProduct != null) {
                append("${worstProduct.productName}可能对皮肤产生了一定负面影响，建议关注。")
            }
        }

        val recommendations = buildList {
            if (topProduct != null) {
                add("继续使用${topProduct.productName}，它对您的皮肤改善效果明显")
            }
            if (worstProduct != null) {
                add("考虑暂停使用${worstProduct.productName}，观察皮肤变化")
            }
            add("建议坚持每日防晒，这是护肤最重要的一步")
            add("保持规律的护肤记录，每周至少记录 3 次以获得更准确的分析")
            add("注意饮食和睡眠对皮肤的影响，保持健康的生活习惯")
        }.take(5)

        return AttributionReport(
            summary = summary,
            overallTrend = trend,
            productRankings = rankings,
            recommendations = recommendations,
            generatedAt = Clock.System.now().toEpochMilliseconds(),
        )
    }

    // ── Skin image mock implementation (fallback) ──────────

    private suspend fun mockAnalyze(): SkinAnalysisResult {
        delay(1500) // Simulate API latency

        val seed = Random.nextInt(100)
        val profile = when {
            seed < 25 -> MockProfile.GOOD
            seed < 50 -> MockProfile.MODERATE
            seed < 75 -> MockProfile.CONCERN
            else -> MockProfile.EXCELLENT
        }

        return SkinAnalysisResult(
            overallScore = profile.overallRange.random(),
            acneCount = profile.acneRange.random(),
            poreScore = profile.poreRange.random(),
            rednessScore = profile.rednessRange.random(),
            evenScore = profile.evenRange.random(),
            blackheadDensity = profile.blackheadRange.random(),
            skinType = profile.skinTypes.random(),
            summary = profile.summaries.random(),
            recommendations = profile.recommendations.shuffled().take(3),
        )
    }
}

private enum class MockProfile(
    val overallRange: IntRange,
    val acneRange: IntRange,
    val poreRange: IntRange,
    val rednessRange: IntRange,
    val evenRange: IntRange,
    val blackheadRange: IntRange,
    val skinTypes: List<SkinType>,
    val summaries: List<String>,
    val recommendations: List<String>,
) {
    EXCELLENT(
        overallRange = 85..95,
        acneRange = 0..1,
        poreRange = 80..95,
        rednessRange = 75..90,
        evenRange = 80..95,
        blackheadRange = 5..15,
        skinTypes = listOf(SkinType.NORMAL, SkinType.COMBINATION),
        summaries = listOf(
            "皮肤状态非常好！肤色均匀，毛孔细腻，几乎没有痘痘。继续保持当前的护肤习惯。",
            "整体肤质优秀，水油平衡良好。肌肤透亮有光泽，建议坚持日常防晒。",
            "皮肤健康度很高，屏障功能完好。面部几乎无瑕疵，继续维持规律作息。",
        ),
        recommendations = listOf(
            "继续当前护肤方案，效果很好",
            "坚持每日防晒 SPF30+",
            "保持充足睡眠和饮水",
            "每周一次温和去角质维持肤质",
            "注意换季期间加强保湿",
        ),
    ),
    GOOD(
        overallRange = 72..84,
        acneRange = 1..3,
        poreRange = 65..80,
        rednessRange = 60..78,
        evenRange = 65..80,
        blackheadRange = 15..30,
        skinTypes = listOf(SkinType.COMBINATION, SkinType.NORMAL),
        summaries = listOf(
            "皮肤状态良好，有轻微的毛孔粗大和少量痘痘。建议加强清洁和保湿。",
            "整体不错，T区略有出油。注意控油同时保持两颊保湿，避免过度清洁。",
            "肤质较好，有少许黑头和细纹。建议使用含烟酰胺的产品改善肤色均匀度。",
        ),
        recommendations = listOf(
            "使用含水杨酸的洁面产品，温和去除黑头",
            "日间使用轻薄保湿+防晒",
            "晚间可使用含视黄醇的精华",
            "每周2-3次使用面膜补水",
            "减少甜食和奶制品的摄入",
            "保持规律运动促进代谢",
        ),
    ),
    MODERATE(
        overallRange = 58..71,
        acneRange = 3..6,
        poreRange = 50..65,
        rednessRange = 45..62,
        evenRange = 50..65,
        blackheadRange = 30..45,
        skinTypes = listOf(SkinType.OILY, SkinType.COMBINATION, SkinType.SENSITIVE),
        summaries = listOf(
            "皮肤有一定问题需要关注：痘痘较多，毛孔偏粗，肤色不太均匀。建议调整护肤方案。",
            "面部出油明显，炎症性痘痘较多。需要控油抗炎，同时注意修复皮肤屏障。",
            "皮肤偏敏感，有泛红和不均匀现象。建议简化护肤步骤，使用温和修复型产品。",
        ),
        recommendations = listOf(
            "简化护肤步骤，避免过多产品叠加",
            "使用含神经酰胺的修复面霜",
            "避免使用含酒精的爽肤水",
            "暂停使用强效酸类产品",
            "保证每天8小时睡眠",
            "减少辛辣刺激食物",
            "考虑咨询皮肤科医生",
        ),
    ),
    CONCERN(
        overallRange = 45..57,
        acneRange = 5..8,
        poreRange = 40..52,
        rednessRange = 35..48,
        evenRange = 38..52,
        blackheadRange = 40..60,
        skinTypes = listOf(SkinType.OILY, SkinType.SENSITIVE),
        summaries = listOf(
            "皮肤问题较为明显：多处痘痘和泛红，毛孔粗大，肤色不均。强烈建议调整护肤方案并考虑就医。",
            "面部炎症较重，皮肤屏障可能受损。需要以修复为主，暂停所有刺激性产品。",
            "皮肤状态需要重点关注。建议就医检查，排除激素或过敏因素，制定针对性方案。",
        ),
        recommendations = listOf(
            "建议就近咨询皮肤科医生",
            "停用所有含酸类和美白产品",
            "仅使用温和洁面+修复面霜+防晒三步",
            "保持面部清洁，不要频繁触碰",
            "记录饮食日记，排查过敏原",
            "调整作息，减少压力",
            "不要挤痘痘，避免留疤",
            "使用含积雪草成分的修复产品",
        ),
    ),
}
