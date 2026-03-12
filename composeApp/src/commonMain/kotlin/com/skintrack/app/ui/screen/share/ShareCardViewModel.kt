package com.skintrack.app.ui.screen.share

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skintrack.app.domain.model.FeatureGate
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.domain.repository.SkinRecordRepository
import com.skintrack.app.domain.usecase.CheckFeatureAccess
import com.skintrack.app.platform.ShareManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ShareCardViewModel(
    private val skinRecordRepository: SkinRecordRepository,
    private val checkFeatureAccess: CheckFeatureAccess,
    private val shareManager: ShareManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<ShareCardUiState>(ShareCardUiState.Loading)
    val uiState: StateFlow<ShareCardUiState> = _uiState.asStateFlow()

    fun loadRecords(beforeId: String, afterId: String) {
        viewModelScope.launch {
            val before = skinRecordRepository.getById(beforeId)
            val after = skinRecordRepository.getById(afterId)

            if (before == null || after == null) {
                _uiState.value = ShareCardUiState.Error("未找到记录")
                return@launch
            }

            val canShare = checkFeatureAccess.canAccess(FeatureGate.SHARE_CARD)

            _uiState.value = ShareCardUiState.Content(
                before = before,
                after = after,
                canShare = canShare,
            )
        }
    }

    fun loadLatestCompare() {
        viewModelScope.launch {
            val records = skinRecordRepository.getRecordsByUser("local-user").first()
            val scored = records.filter { it.overallScore != null }.sortedBy { it.recordedAt }

            if (scored.size < 2) {
                _uiState.value = ShareCardUiState.Error("至少需要 2 条有评分的记录")
                return@launch
            }

            val canShare = checkFeatureAccess.canAccess(FeatureGate.SHARE_CARD)

            _uiState.value = ShareCardUiState.Content(
                before = scored.first(),
                after = scored.last(),
                canShare = canShare,
            )
        }
    }

    fun share() {
        val state = _uiState.value
        if (state !is ShareCardUiState.Content) return

        viewModelScope.launch {
            // Mock share — in real implementation would capture composable to bitmap
            val scoreDiff = (state.after.overallScore ?: 0) - (state.before.overallScore ?: 0)
            val prefix = if (scoreDiff >= 0) "+" else ""
            val text = "SkinTrack 皮肤变化：评分 ${state.before.overallScore} → ${state.after.overallScore}（${prefix}$scoreDiff）"
            shareManager.shareImage(byteArrayOf(), text)
        }
    }
}

sealed interface ShareCardUiState {
    data object Loading : ShareCardUiState
    data class Content(
        val before: SkinRecord,
        val after: SkinRecord,
        val canShare: Boolean = true,
    ) : ShareCardUiState
    data class Error(val message: String) : ShareCardUiState
}
