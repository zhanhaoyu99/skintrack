package com.skintrack.app.ui.screen.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.domain.repository.AuthRepository
import com.skintrack.app.domain.repository.SkinRecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.days

class TimelineViewModel(
    private val skinRecordRepository: SkinRecordRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<TimelineUiState>(TimelineUiState.Loading)
    val uiState: StateFlow<TimelineUiState> = _uiState.asStateFlow()

    private val _selectedFilter = MutableStateFlow(TimelineFilter.ALL)
    val selectedFilter: StateFlow<TimelineFilter> = _selectedFilter.asStateFlow()

    private val _selectedMetric = MutableStateFlow(ChartMetric.OVERALL)
    val selectedMetric: StateFlow<ChartMetric> = _selectedMetric.asStateFlow()

    fun setFilter(filter: TimelineFilter) {
        _selectedFilter.value = filter
    }

    fun setMetric(metric: ChartMetric) {
        _selectedMetric.value = metric
    }

    init {
        viewModelScope.launch {
            val userId = authRepository.currentUser()?.userId ?: "local-user"
            combine(
                skinRecordRepository.getRecordsByUser(userId),
                _selectedFilter,
            ) { records, filter ->
                if (records.isEmpty()) TimelineUiState.Empty
                else {
                    val now = Clock.System.now()
                    val filteredRecords = when (filter) {
                        TimelineFilter.ALL -> records
                        TimelineFilter.WEEK -> records.filter { it.recordedAt > now - 7.days }
                        TimelineFilter.MONTH -> records.filter { it.recordedAt > now - 30.days }
                        TimelineFilter.THREE_MONTHS -> records.filter { it.recordedAt > now - 90.days }
                    }

                    if (filteredRecords.isEmpty()) TimelineUiState.Empty
                    else {
                        val scoredRecords = filteredRecords
                            .filter { it.overallScore != null }
                            .sortedBy { it.recordedAt }

                        val compareData = if (scoredRecords.size >= 2)
                            CompareData(before = scoredRecords.first(), after = scoredRecords.last())
                        else null

                        TimelineUiState.Content(
                            records = filteredRecords,
                            chartPoints = scoredRecords
                                .map {
                                    ChartRecord(
                                        date = it.recordedAt,
                                        overallScore = it.overallScore!!,
                                        acneCount = it.acneCount,
                                        poreScore = it.poreScore,
                                        evenScore = it.evenScore,
                                        hydrationScore = null, // blackheadDensity used as proxy
                                    )
                                },
                            compareData = compareData,
                        )
                    }
                }
            }.collect { _uiState.value = it }
        }
    }
}

enum class TimelineFilter(val label: String) {
    ALL("全部"), WEEK("本周"), MONTH("本月"), THREE_MONTHS("3个月")
}

sealed interface TimelineUiState {
    data object Loading : TimelineUiState
    data object Empty : TimelineUiState
    data class Content(
        val records: List<SkinRecord>,
        val chartPoints: List<ChartRecord>,
        val compareData: CompareData? = null,
    ) : TimelineUiState
}
