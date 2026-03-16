package com.skintrack.app.ui.screen.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skintrack.app.data.remote.AiAnalysisService
import com.skintrack.app.data.remote.SkinAnalysisResult
import com.skintrack.app.data.remote.SyncManager
import com.skintrack.app.domain.model.FeatureGate
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.domain.model.SkinType
import com.skintrack.app.domain.model.lockedMessage
import com.skintrack.app.domain.repository.AuthRepository
import com.skintrack.app.domain.repository.SkinRecordRepository
import com.skintrack.app.domain.usecase.CheckFeatureAccess
import com.skintrack.app.domain.usecase.UpdateCheckInStreak
import com.skintrack.app.platform.CameraController
import com.skintrack.app.platform.ImageCompressor
import com.skintrack.app.platform.ImageStorage
import kotlinx.coroutines.delay
import com.skintrack.app.ui.component.SnackbarMessage
import com.skintrack.app.ui.component.SnackbarType
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class CameraViewModel(
    private val imageCompressor: ImageCompressor,
    private val imageStorage: ImageStorage,
    private val skinRecordRepository: SkinRecordRepository,
    private val aiAnalysisService: AiAnalysisService,
    private val checkFeatureAccess: CheckFeatureAccess,
    private val updateCheckInStreak: UpdateCheckInStreak,
    private val authRepository: AuthRepository,
    private val syncManager: SyncManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CameraUiState>(CameraUiState.Previewing)
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<SnackbarMessage>(extraBufferCapacity = 1)
    val snackbarEvent: SharedFlow<SnackbarMessage> = _snackbarEvent.asSharedFlow()

    private var cameraController: CameraController? = null

    fun onCameraReady(controller: CameraController) {
        cameraController = controller
    }

    fun capture() {
        val controller = cameraController ?: return
        if (_uiState.value != CameraUiState.Previewing) return

        _uiState.value = CameraUiState.Capturing

        viewModelScope.launch {
            val photoBytes = controller.takePhoto()
            if (photoBytes != null) {
                _uiState.value = CameraUiState.Confirming(photoBytes)
            } else {
                _uiState.value = CameraUiState.Previewing
            }
        }
    }

    fun retake() {
        _uiState.value = CameraUiState.Previewing
    }

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    @OptIn(ExperimentalUuidApi::class)
    fun confirm() {
        val state = _uiState.value
        if (state !is CameraUiState.Confirming) return
        if (_isProcessing.value) return

        _isProcessing.value = true
        _uiState.value = CameraUiState.Saving

        viewModelScope.launch {
            try {
                // Check record limit for free users
                if (!checkFeatureAccess.canAccess(FeatureGate.UNLIMITED_RECORDS)) {
                    _uiState.value = CameraUiState.FeatureGated(
                        FeatureGate.UNLIMITED_RECORDS.lockedMessage
                    )
                    return@launch
                }
                val user = authRepository.currentUser()
                val userId = user?.userId ?: "local-user"

                val compressed = imageCompressor.compress(state.photoBytes)
                val id = Uuid.random().toString()
                val fileName = "skin_$id.jpg"
                val localPath = imageStorage.saveImage(compressed, fileName)
                val now = Clock.System.now()

                // Upload to remote storage (non-blocking fallback)
                val imageUrl = skinRecordRepository.uploadImage(userId, fileName, compressed)

                val record = SkinRecord(
                    id = id,
                    userId = userId,
                    skinType = SkinType.UNKNOWN,
                    localImagePath = localPath,
                    imageUrl = imageUrl,
                    recordedAt = now,
                    createdAt = now,
                )
                skinRecordRepository.save(record)

                // AI analysis phase with exponential backoff retry
                _uiState.value = CameraUiState.Analyzing()

                val result = retryAnalysis(compressed)
                if (result != null) {
                    val updatedRecord = record.copy(
                        overallScore = result.overallScore,
                        acneCount = result.acneCount,
                        poreScore = result.poreScore,
                        rednessScore = result.rednessScore,
                        evenScore = result.evenScore,
                        blackheadDensity = result.blackheadDensity,
                        skinType = result.skinType,
                        analysisJson = result.toJson(),
                    )
                    skinRecordRepository.save(updatedRecord)
                }
                // Background sync to Supabase
                syncManager.pushChanges()
                val milestoneMessage = updateCheckInStreak.onNewRecord()
                _uiState.value = CameraUiState.Saved(result, milestoneMessage, id)
                _snackbarEvent.tryEmit(
                    SnackbarMessage("拍照记录成功", SnackbarType.SUCCESS)
                )
            } catch (e: Exception) {
                _uiState.value = CameraUiState.Error(e.message ?: "保存失败")
                _snackbarEvent.tryEmit(
                    SnackbarMessage(e.message ?: "保存失败", SnackbarType.ERROR)
                )
            } finally {
                _isProcessing.value = false
            }
        }
    }

    private suspend fun retryAnalysis(imageBytes: ByteArray): SkinAnalysisResult? {
        val maxRetries = 3
        var delayMs = 1000L
        for (attempt in 1..maxRetries) {
            try {
                if (attempt > 1) {
                    _uiState.value = CameraUiState.Analyzing(retryCount = attempt)
                }
                return aiAnalysisService.analyzeSkinImage(imageBytes)
            } catch (e: Exception) {
                io.github.aakira.napier.Napier.w("AI analysis attempt $attempt failed: ${e.message}")
                if (attempt < maxRetries) {
                    delay(delayMs)
                    delayMs *= 2
                }
            }
        }
        // All retries exhausted — record saved as "pending analysis"
        io.github.aakira.napier.Napier.w("AI analysis failed after $maxRetries attempts, saving as pending")
        return null
    }

    fun resetToPreview() {
        _uiState.value = CameraUiState.Previewing
    }
}

private fun SkinAnalysisResult.toJson(): String = buildJsonObject {
    put("overallScore", overallScore)
    put("acneCount", acneCount)
    put("poreScore", poreScore)
    put("rednessScore", rednessScore)
    put("evenScore", evenScore)
    put("blackheadDensity", blackheadDensity)
    put("skinType", skinType.name)
    put("summary", summary)
    putJsonArray("recommendations") {
        recommendations.forEach { add(JsonPrimitive(it)) }
    }
}.toString()

sealed interface CameraUiState {
    data object Previewing : CameraUiState
    data object Capturing : CameraUiState
    data class Confirming(val photoBytes: ByteArray) : CameraUiState
    data object Saving : CameraUiState
    data class Analyzing(val retryCount: Int = 0) : CameraUiState
    data class Saved(
        val analysisResult: SkinAnalysisResult?,
        val milestoneMessage: String? = null,
        val recordId: String? = null,
    ) : CameraUiState
    data class FeatureGated(val message: String) : CameraUiState
    data class Error(val message: String) : CameraUiState
}
