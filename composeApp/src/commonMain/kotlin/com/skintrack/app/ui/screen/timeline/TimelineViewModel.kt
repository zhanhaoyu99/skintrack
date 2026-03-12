package com.skintrack.app.ui.screen.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.domain.repository.AuthRepository
import com.skintrack.app.domain.repository.SkinRecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class TimelineViewModel(
    private val skinRecordRepository: SkinRecordRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<TimelineUiState>(TimelineUiState.Loading)
    val uiState: StateFlow<TimelineUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val userId = authRepository.currentUser()?.userId ?: "local-user"
            skinRecordRepository.getRecordsByUser(userId)
                .map { records ->
                    if (records.isEmpty()) TimelineUiState.Empty
                    else {
                        val scoredRecords = records
                            .filter { it.overallScore != null }
                            .sortedBy { it.recordedAt }

                        val compareData = if (scoredRecords.size >= 2)
                            CompareData(before = scoredRecords.first(), after = scoredRecords.last())
                        else null

                        TimelineUiState.Content(
                            records = records,
                            chartPoints = scoredRecords
                                .map { ChartRecord(date = it.recordedAt, overallScore = it.overallScore!!) },
                            compareData = compareData,
                        )
                    }
                }
                .collect { _uiState.value = it }
        }
    }
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
