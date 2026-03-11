package com.skintrack.app.platform

import androidx.compose.runtime.Composable

enum class PermissionStatus {
    Granted,
    Denied,
    NotDetermined,
}

@Composable
expect fun rememberCameraPermissionState(): CameraPermissionState

expect class CameraPermissionState {
    val status: PermissionStatus
    fun requestPermission()
}
