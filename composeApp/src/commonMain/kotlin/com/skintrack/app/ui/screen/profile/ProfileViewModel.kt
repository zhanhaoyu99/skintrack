package com.skintrack.app.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skintrack.app.domain.repository.ProductRepository
import com.skintrack.app.domain.repository.SkinRecordRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ProfileViewModel(
    skinRecordRepository: SkinRecordRepository,
    productRepository: ProductRepository,
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        skinRecordRepository.getRecordsByUser("local-user"),
        productRepository.getAllProducts(),
    ) { records, products ->
        val scoredRecords = records.filter { it.overallScore != null }
        ProfileUiState.Content(
            totalRecords = records.size,
            totalProducts = products.size,
            latestScore = scoredRecords
                .maxByOrNull { it.recordedAt }
                ?.overallScore,
            averageScore = scoredRecords
                .takeIf { it.isNotEmpty() }
                ?.let { list -> list.sumOf { it.overallScore!! } / list.size },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState.Loading)
}

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Content(
        val totalRecords: Int,
        val totalProducts: Int,
        val latestScore: Int?,
        val averageScore: Int?,
    ) : ProfileUiState
}
