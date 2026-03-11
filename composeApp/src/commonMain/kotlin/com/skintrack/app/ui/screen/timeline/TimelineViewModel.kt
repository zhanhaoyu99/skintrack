package com.skintrack.app.ui.screen.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.domain.repository.SkinRecordRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class TimelineViewModel(
    skinRecordRepository: SkinRecordRepository,
) : ViewModel() {

    val uiState: StateFlow<TimelineUiState> = skinRecordRepository
        .getRecordsByUser("local-user")
        .map { records ->
            if (records.isEmpty()) TimelineUiState.Empty
            else TimelineUiState.Content(records)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TimelineUiState.Loading)
}

sealed interface TimelineUiState {
    data object Loading : TimelineUiState
    data object Empty : TimelineUiState
    data class Content(val records: List<SkinRecord>) : TimelineUiState
}
