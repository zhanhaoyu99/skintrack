package com.skintrack.app.platform

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager

@Composable
actual fun rememberCameraPermissionState(): CameraPermissionState {
    val context = LocalContext.current
    var status by remember {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA,
        ) == PackageManager.PERMISSION_GRANTED
        mutableStateOf(if (granted) PermissionStatus.Granted else PermissionStatus.NotDetermined)
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        status = if (isGranted) PermissionStatus.Granted else PermissionStatus.Denied
    }

    return remember(status) {
        CameraPermissionState(
            status = status,
            onRequest = { launcher.launch(Manifest.permission.CAMERA) },
        )
    }
}

actual class CameraPermissionState(
    actual val status: PermissionStatus,
    private val onRequest: () -> Unit,
) {
    actual fun requestPermission() {
        onRequest()
    }
}
