package com.skintrack.app.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CameraPreview(
    modifier: Modifier = Modifier,
    onCameraReady: (CameraController) -> Unit = {},
)
