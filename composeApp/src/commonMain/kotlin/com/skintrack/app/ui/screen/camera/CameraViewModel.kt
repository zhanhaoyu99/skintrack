package com.skintrack.app.ui.screen.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.domain.model.SkinType
import com.skintrack.app.domain.repository.SkinRecordRepository
import com.skintrack.app.platform.CameraController
import com.skintrack.app.platform.ImageCompressor
import com.skintrack.app.platform.ImageStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class CameraViewModel(
    private val imageCompressor: ImageCompressor,
    private val imageStorage: ImageStorage,
    private val skinRecordRepository: SkinRecordRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow<CameraUiState>(CameraUiState.Previewing)
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

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

    @OptIn(ExperimentalUuidApi::class)
    fun confirm() {
        val state = _uiState.value
        if (state !is CameraUiState.Confirming) return

        _uiState.value = CameraUiState.Saving

        viewModelScope.launch {
            try {
                val compressed = imageCompressor.compress(state.photoBytes)
                val id = Uuid.random().toString()
                val fileName = "skin_$id.jpg"
                val localPath = imageStorage.saveImage(compressed, fileName)
                val now = Clock.System.now()

                val record = SkinRecord(
                    id = id,
                    userId = "local-user",
                    skinType = SkinType.UNKNOWN,
                    localImagePath = localPath,
                    recordedAt = now,
                    createdAt = now,
                )
                skinRecordRepository.save(record)

                _uiState.value = CameraUiState.Saved
                delay(2000)
                _uiState.value = CameraUiState.Previewing
            } catch (e: Exception) {
                _uiState.value = CameraUiState.Error(e.message ?: "保存失败")
            }
        }
    }

    fun resetToPreview() {
        _uiState.value = CameraUiState.Previewing
    }
}

sealed interface CameraUiState {
    data object Previewing : CameraUiState
    data object Capturing : CameraUiState
    data class Confirming(val photoBytes: ByteArray) : CameraUiState
    data object Saving : CameraUiState
    data object Saved : CameraUiState
    data class Error(val message: String) : CameraUiState
}
