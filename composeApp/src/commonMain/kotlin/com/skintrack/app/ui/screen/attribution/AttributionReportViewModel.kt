package com.skintrack.app.ui.screen.attribution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skintrack.app.domain.model.ProductAttribution
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.domain.model.SkincareProduct
import com.skintrack.app.domain.repository.AuthRepository
import com.skintrack.app.domain.repository.ProductRepository
import com.skintrack.app.domain.repository.SkinRecordRepository
import com.skintrack.app.domain.usecase.CheckFeatureAccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime

class AttributionReportViewModel(
    private val skinRecordRepository: SkinRecordRepository,
    private val productRepository: ProductRepository,
    private val checkFeatureAccess: CheckFeatureAccess,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AttributionUiState>(AttributionUiState.Loading)
    val uiState: StateFlow<AttributionUiState> = _uiState

    init {
        loadReport()
    }

    private fun loadReport() {
        viewModelScope.launch {
            val userId = authRepository.currentUser()?.userId ?: "local-user"
            val records = skinRecordRepository.getRecordsByUser(userId).first()
            val scoredRecords = records
                .filter { it.overallScore != null }
                .sortedBy { it.recordedAt }

            if (scoredRecords.size < 3) {
                _uiState.value = AttributionUiState.InsufficientData
                return@launch
            }

            val tz = TimeZone.currentSystemDefault()
            val startDate = scoredRecords.first().recordedAt.toLocalDateTime(tz).date
            val endDate = scoredRecords.last().recordedAt.toLocalDateTime(tz).date

            val usages = productRepository.getUsageBetween(userId, startDate, endDate).first()
            val allProducts = productRepository.getAllProducts().first()
            val productMap = allProducts.associateBy { it.id }

            val attributions = calculateAttributions(scoredRecords, usages, productMap, tz)

            val firstScore = scoredRecords.first().overallScore!!
            val lastScore = scoredRecords.last().overallScore!!
            val trendDelta = lastScore - firstScore
            val overallTrend = when {
                trendDelta > 2 -> "上升"
                trendDelta < -2 -> "下降"
                else -> "稳定"
            }

            val dateRange = "${formatDate(startDate)} — ${formatDate(endDate)}"

            val isPremium = checkFeatureAccess.isPremium()

            val analysisDays = startDate.daysUntil(endDate).coerceAtLeast(1)
            val productsUsed = attributions.size

            val suggestions = buildSuggestions(attributions, overallTrend)
            val aiInsight = buildAiInsight(firstScore, lastScore, analysisDays, attributions)

            _uiState.value = AttributionUiState.Content(
                totalRecords = scoredRecords.size,
                dateRange = dateRange,
                overallTrend = overallTrend,
                trendDelta = trendDelta,
                attributions = attributions,
                isPremium = isPremium,
                productsUsed = productsUsed,
                analysisDays = analysisDays,
                suggestions = suggestions,
                firstScore = firstScore,
                lastScore = lastScore,
                aiInsight = aiInsight,
            )
        }
    }

    private fun calculateAttributions(
        scoredRecords: List<SkinRecord>,
        usages: List<com.skintrack.app.domain.model.DailyProductUsage>,
        productMap: Map<String, SkincareProduct>,
        tz: TimeZone,
    ): List<ProductAttribution> {
        // Map records by date → score
        val scoreByDate = scoredRecords.associate { record ->
            record.recordedAt.toLocalDateTime(tz).date to record.overallScore!!
        }

        // Group usage dates by product
        val usageDatesByProduct = usages.groupBy { it.productId }
            .mapValues { (_, list) -> list.map { it.usedDate }.toSet() }

        val allDates = scoreByDate.keys

        return usageDatesByProduct.mapNotNull { (productId, usedDates) ->
            val product = productMap[productId] ?: return@mapNotNull null
            val datesWithScore = allDates.filter { it in usedDates }
            val datesWithoutScore = allDates.filter { it !in usedDates }

            if (datesWithScore.isEmpty() || datesWithoutScore.isEmpty()) return@mapNotNull null

            val avgUsed = datesWithScore.map { scoreByDate[it]!! }.average().toFloat()
            val avgNotUsed = datesWithoutScore.map { scoreByDate[it]!! }.average().toFloat()

            ProductAttribution(
                product = product,
                daysUsed = usedDates.size,
                avgScoreWhenUsed = avgUsed,
                avgScoreWhenNotUsed = avgNotUsed,
                impact = avgUsed - avgNotUsed,
            )
        }.sortedByDescending { it.impact }
    }

    private fun buildSuggestions(
        attributions: List<ProductAttribution>,
        overallTrend: String,
    ): List<String> {
        val suggestions = mutableListOf<String>()

        // Suggestion based on best product
        val bestProduct = attributions.firstOrNull { it.impact > 0 }
        if (bestProduct != null) {
            suggestions.add("继续使用「${bestProduct.product.name}」，它对你的肤质有明显的正面影响")
        }

        // Suggestion based on worst product
        val worstProduct = attributions.lastOrNull { it.impact < 0 }
        if (worstProduct != null) {
            suggestions.add("考虑减少或停用「${worstProduct.product.name}」，数据显示它可能对你的皮肤状态有负面影响")
        }

        // General suggestion based on trend
        when (overallTrend) {
            "上升" -> suggestions.add("你的皮肤状态正在改善，保持当前护肤习惯并坚持记录")
            "下降" -> suggestions.add("建议检查近期的作息和饮食习惯，同时简化护肤步骤观察变化")
            else -> suggestions.add("保持规律的拍照记录，更多数据有助于发现护肤产品的真实效果")
        }

        return suggestions.take(3)
    }

    private fun buildAiInsight(
        firstScore: Int,
        lastScore: Int,
        analysisDays: Int,
        attributions: List<ProductAttribution>,
    ): String {
        val delta = lastScore - firstScore
        val direction = when {
            delta > 0 -> "提升"
            delta < 0 -> "下降"
            else -> "保持稳定"
        }
        val pct = if (firstScore > 0) {
            (kotlin.math.abs(delta) * 100 / firstScore)
        } else {
            0
        }
        val topProducts = attributions.filter { it.impact > 0 }.take(2)
        val productPart = if (topProducts.isNotEmpty()) {
            topProducts.joinToString("和") { it.product.name } + "的组合对你很有效。"
        } else {
            ""
        }
        return "过去 $analysisDays 天你的皮肤整体评分从 $firstScore ${direction}到了 $lastScore，" +
            "变化了 ${pct}%。$productPart" +
            "建议保持当前的护肤方案，持续记录观察变化。"
    }

    private fun formatDate(date: kotlinx.datetime.LocalDate): String {
        val year = date.year
        val month = date.monthNumber.toString().padStart(2, '0')
        val day = date.dayOfMonth.toString().padStart(2, '0')
        return "$year.$month.$day"
    }
}

sealed interface AttributionUiState {
    data object Loading : AttributionUiState
    data object InsufficientData : AttributionUiState
    data class Content(
        val totalRecords: Int,
        val dateRange: String,
        val overallTrend: String,
        val trendDelta: Int,
        val attributions: List<ProductAttribution>,
        val isPremium: Boolean = true,
        val productsUsed: Int = 0,
        val analysisDays: Int = 14,
        val suggestions: List<String> = emptyList(),
        val firstScore: Int = 0,
        val lastScore: Int = 0,
        val aiInsight: String = "",
    ) : AttributionUiState
}
