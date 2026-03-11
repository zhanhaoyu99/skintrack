package com.skintrack.app.ui.screen.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skintrack.app.platform.CameraController
import com.skintrack.app.platform.ImageCompressor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CameraViewModel(
    private val imageCompressor: ImageCompressor,
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

    fun confirm() {
        val state = _uiState.value
        if (state !is CameraUiState.Confirming) return

        _uiState.value = CameraUiState.Saving

        viewModelScope.launch {
            val compressed = imageCompressor.compress(state.photoBytes)
            _uiState.value = CameraUiState.Saved(compressed)
        }
    }
}

sealed interface CameraUiState {
    data object Previewing : CameraUiState
    data object Capturing : CameraUiState
    data class Confirming(val photoBytes: ByteArray) : CameraUiState
    data object Saving : CameraUiState
    data class Saved(val compressedBytes: ByteArray) : CameraUiState
}
