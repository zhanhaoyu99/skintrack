package com.skintrack.app.ui.screen.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.skintrack.app.platform.CameraPreview
import com.skintrack.app.platform.PermissionStatus
import com.skintrack.app.platform.rememberCameraPermissionState
import com.skintrack.app.ui.component.ErrorContent
import com.skintrack.app.ui.component.LoadingContent
import com.skintrack.app.ui.theme.extendedColors
import com.skintrack.app.ui.theme.spacing
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CameraScreen(
    viewModel: CameraViewModel = koinViewModel(),
) {
    val permissionState = rememberCameraPermissionState()

    when (permissionState.status) {
        PermissionStatus.Granted -> {
            CameraContent(viewModel)
        }
        PermissionStatus.NotDetermined -> {
            PermissionRequestContent(
                onRequest = { permissionState.requestPermission() },
            )
        }
        PermissionStatus.Denied -> {
            PermissionDeniedContent()
        }
    }
}

@Composable
private fun CameraContent(viewModel: CameraViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    when (val state = uiState) {
        is CameraUiState.Previewing,
        is CameraUiState.Capturing -> {
            Box(modifier = Modifier.fillMaxSize()) {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    onCameraReady = viewModel::onCameraReady,
                )

                FaceGuideOverlay()

                // Capture button
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = MaterialTheme.spacing.xxl),
                ) {
                    CaptureButton(
                        onClick = viewModel::capture,
                        enabled = state is CameraUiState.Previewing,
                    )
                }
            }
        }
        is CameraUiState.Confirming -> {
            PhotoConfirmContent(
                photoBytes = state.photoBytes,
                onConfirm = viewModel::confirm,
                onRetake = viewModel::retake,
            )
        }
        is CameraUiState.Saving -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                LoadingContent(message = "保存中...")
            }
        }
        is CameraUiState.Saved -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(
                    MaterialTheme.spacing.md,
                    Alignment.CenterVertically,
                ),
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(MaterialTheme.spacing.section),
                    tint = MaterialTheme.extendedColors.functional.success,
                )
                Text(
                    text = "保存成功",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = "照片已保存到本地记录",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        is CameraUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                ErrorContent(
                    message = state.message,
                    onRetry = viewModel::resetToPreview,
                )
            }
        }
    }
}

@Composable
private fun CaptureButton(
    onClick: () -> Unit,
    enabled: Boolean,
) {
    val cameraColors = MaterialTheme.extendedColors.camera
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .border(4.dp, cameraColors.buttonBorder, CircleShape)
            .padding(6.dp)
            .clip(CircleShape)
            .background(if (enabled) cameraColors.buttonFill else cameraColors.buttonDisabled)
            .clickable(enabled = enabled, onClick = onClick),
    )
}

@Composable
private fun PhotoConfirmContent(
    photoBytes: ByteArray,
    onConfirm: () -> Unit,
    onRetake: () -> Unit,
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.extendedColors.camera.background),
    ) {
        AsyncImage(
            model = photoBytes,
            contentDescription = "拍摄照片",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentScale = ContentScale.Fit,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.lg, vertical = spacing.lg),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            OutlinedButton(
                onClick = onRetake,
                modifier = Modifier.weight(1f),
            ) {
                Text("重拍")
            }
            Button(
                onClick = onConfirm,
                modifier = Modifier.weight(1f),
            ) {
                Text("使用照片")
            }
        }
    }
}

@Composable
private fun PermissionRequestContent(onRequest: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "需要相机权限来拍摄皮肤照片",
            style = MaterialTheme.typography.bodyLarge,
        )
        Button(
            onClick = onRequest,
            modifier = Modifier.padding(top = MaterialTheme.spacing.md),
        ) {
            Text("授予权限")
        }
    }
}

@Composable
private fun PermissionDeniedContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "相机权限被拒绝",
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = "请在系统设置中开启相机权限",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = MaterialTheme.spacing.sm),
        )
    }
}
