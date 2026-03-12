package com.skintrack.app.ui.screen.attribution

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skintrack.app.domain.model.ProductAttribution
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.domain.model.SkincareProduct
import com.skintrack.app.domain.repository.ProductRepository
import com.skintrack.app.domain.repository.SkinRecordRepository
import com.skintrack.app.domain.usecase.CheckFeatureAccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class AttributionReportViewModel(
    private val skinRecordRepository: SkinRecordRepository,
    private val productRepository: ProductRepository,
    private val checkFeatureAccess: CheckFeatureAccess,
) : ViewModel() {

    private val _uiState = MutableStateFlow<AttributionUiState>(AttributionUiState.Loading)
    val uiState: StateFlow<AttributionUiState> = _uiState

    init {
        loadReport()
    }

    private fun loadReport() {
        viewModelScope.launch {
            val records = skinRecordRepository.getRecordsByUser("local-user").first()
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

            val usages = productRepository.getUsageBetween("local-user", startDate, endDate).first()
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

            _uiState.value = AttributionUiState.Content(
                totalRecords = scoredRecords.size,
                dateRange = dateRange,
                overallTrend = overallTrend,
                trendDelta = trendDelta,
                attributions = attributions,
                isPremium = isPremium,
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
    ) : AttributionUiState
}
