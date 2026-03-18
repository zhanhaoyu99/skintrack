package com.skintrack.app.ui.screen.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.domain.model.SkincareProduct
import com.skintrack.app.domain.repository.ProductRepository
import com.skintrack.app.domain.repository.SkinRecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.skintrack.app.domain.usecase.CheckFeatureAccess
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class RecordDetailViewModel(
    private val skinRecordRepository: SkinRecordRepository,
    private val productRepository: ProductRepository,
    private val checkFeatureAccess: CheckFeatureAccess,
) : ViewModel() {

    private val _uiState = MutableStateFlow<RecordDetailUiState>(RecordDetailUiState.Loading)
    val uiState: StateFlow<RecordDetailUiState> = _uiState.asStateFlow()

    fun loadRecord(recordId: String) {
        viewModelScope.launch {
            _uiState.value = RecordDetailUiState.Loading

            val record = skinRecordRepository.getById(recordId)
            if (record == null) {
                _uiState.value = RecordDetailUiState.NotFound
                return@launch
            }

            val (summary, recommendations) = parseAnalysisJson(record.analysisJson)

            val date = record.recordedAt
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .date
            val usages = productRepository.getUsageByDate(record.userId, date).first()
            val products = usages.mapNotNull { usage ->
                productRepository.getProductById(usage.productId)
            }

            val isPremium = checkFeatureAccess.isPremium()

            // Calculate score change from previous record
            val allRecords = skinRecordRepository.getRecordsByUser(record.userId).first()
            val sortedRecords = allRecords.filter { it.overallScore != null }.sortedByDescending { it.recordedAt }
            val currentIndex = sortedRecords.indexOfFirst { it.id == record.id }
            val previousScore = if (currentIndex >= 0 && currentIndex < sortedRecords.lastIndex) {
                sortedRecords[currentIndex + 1].overallScore
            } else null
            val scoreDiff = if (record.overallScore != null && previousScore != null) {
                record.overallScore - previousScore
            } else null

            // Calculate per-metric diffs from previous record
            val previousRecord = if (currentIndex >= 0 && currentIndex < sortedRecords.lastIndex) {
                sortedRecords[currentIndex + 1]
            } else null
            val metricDiffs = if (previousRecord != null) {
                RecordDetailUiState.MetricDiffs(
                    acne = computeAcneDiff(record.acneCount, previousRecord.acneCount),
                    pore = diffOrNull(record.poreScore, previousRecord.poreScore),
                    evenness = diffOrNull(record.evenScore, previousRecord.evenScore),
                    redness = diffOrNull(record.rednessScore, previousRecord.rednessScore),
                    hydration = diffOrNull(record.blackheadDensity, previousRecord.blackheadDensity),
                )
            } else RecordDetailUiState.MetricDiffs()

            // Estimate percentile from score (simple heuristic)
            val percentile = record.overallScore?.let { score ->
                when {
                    score >= 90 -> 95
                    score >= 80 -> 78
                    score >= 70 -> 60
                    score >= 60 -> 40
                    score >= 50 -> 25
                    else -> 10
                }
            }

            _uiState.value = RecordDetailUiState.Content(
                record = record,
                summary = summary,
                recommendations = recommendations,
                usedProducts = products,
                isPremium = isPremium,
                scoreDiff = scoreDiff,
                metricDiffs = metricDiffs,
                percentile = percentile,
            )
        }
    }

    private fun diffOrNull(current: Int?, previous: Int?): Int? =
        if (current != null && previous != null) current - previous else null

    private fun computeAcneDiff(currentAcne: Int?, previousAcne: Int?): Int? {
        if (currentAcne == null || previousAcne == null) return null
        // Convert to normalized score (lower acne = higher score)
        val currentNorm = (100 - currentAcne * 5).coerceIn(0, 100)
        val previousNorm = (100 - previousAcne * 5).coerceIn(0, 100)
        return currentNorm - previousNorm
    }

    private fun parseAnalysisJson(json: String?): Pair<String?, List<String>> {
        if (json == null) return null to emptyList()
        return try {
            val obj = Json.parseToJsonElement(json).jsonObject
            val summary = obj["summary"]?.jsonPrimitive?.content
            val recs = obj["recommendations"]?.jsonArray?.map {
                it.jsonPrimitive.content
            } ?: emptyList()
            summary to recs
        } catch (_: Exception) {
            null to emptyList()
        }
    }
}

sealed interface RecordDetailUiState {
    data object Loading : RecordDetailUiState
    data object NotFound : RecordDetailUiState
    data class Content(
        val record: SkinRecord,
        val summary: String?,
        val recommendations: List<String>,
        val usedProducts: List<SkincareProduct>,
        val isPremium: Boolean = true,
        val scoreDiff: Int? = null,
        val metricDiffs: MetricDiffs = MetricDiffs(),
        val percentile: Int? = null,
    ) : RecordDetailUiState

    data class MetricDiffs(
        val acne: Int? = null,
        val pore: Int? = null,
        val evenness: Int? = null,
        val redness: Int? = null,
        val hydration: Int? = null,
    )
}
