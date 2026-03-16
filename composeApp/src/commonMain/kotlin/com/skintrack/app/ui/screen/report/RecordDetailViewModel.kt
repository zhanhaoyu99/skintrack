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

            _uiState.value = RecordDetailUiState.Content(
                record = record,
                summary = summary,
                recommendations = recommendations,
                usedProducts = products,
                isPremium = isPremium,
                scoreDiff = scoreDiff,
            )
        }
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
    ) : RecordDetailUiState
}
