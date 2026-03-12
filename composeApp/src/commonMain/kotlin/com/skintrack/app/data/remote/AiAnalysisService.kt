package com.skintrack.app.data.remote

import com.skintrack.app.domain.model.SkinType
import io.ktor.client.HttpClient
import kotlinx.coroutines.delay
import kotlin.random.Random

class AiAnalysisService(
    private val httpClient: HttpClient,
) {
    /**
     * Analyze skin image via multimodal LLM API.
     * Currently uses mock data; replace [mockAnalyze] with [realAnalyze] when ready.
     */
    suspend fun analyzeSkinImage(imageBytes: ByteArray): SkinAnalysisResult {
        return mockAnalyze()
    }

    // ── Mock implementation ────────────────────────────
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

    // TODO: Real LLM API call (GPT-4o / Gemini / Claude)
    // private suspend fun realAnalyze(imageBytes: ByteArray): SkinAnalysisResult {
    //     val base64 = Base64.encode(imageBytes)
    //     val response = httpClient.post("https://api.openai.com/v1/chat/completions") {
    //         header("Authorization", "Bearer $apiKey")
    //         contentType(ContentType.Application.Json)
    //         setBody(buildJsonObject {
    //             put("model", "gpt-4o")
    //             putJsonArray("messages") { ... }
    //         })
    //     }
    //     return parseResponse(response.body())
    // }
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
