package com.skintrack.app.ui.screen.camera

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.skintrack.app.platform.CameraPreview
import com.skintrack.app.platform.PermissionStatus
import com.skintrack.app.platform.rememberCameraPermissionState
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.skintrack.app.ui.component.AppSnackbarHost
import com.skintrack.app.ui.component.ErrorContent
import com.skintrack.app.ui.component.LoadingContent
import com.skintrack.app.ui.component.ScoreRing
import com.skintrack.app.ui.component.showTyped
import com.skintrack.app.ui.screen.paywall.PaywallScreen
import com.skintrack.app.ui.screen.report.RecordDetailScreen
import com.skintrack.app.ui.theme.Apricot100
import com.skintrack.app.ui.theme.Apricot300
import com.skintrack.app.ui.theme.Apricot50
import com.skintrack.app.ui.theme.Lavender300
import com.skintrack.app.ui.theme.Lavender50
import com.skintrack.app.ui.theme.Mint50
import com.skintrack.app.ui.theme.Rose100
import com.skintrack.app.ui.theme.Rose400
import com.skintrack.app.ui.theme.Rose50
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.extendedColors
import com.skintrack.app.ui.theme.spacing
import cafe.adriel.voyager.core.screen.Screen
import org.koin.compose.viewmodel.koinViewModel

class CameraScreen : Screen {
    @Composable
    override fun Content() {
        CameraScreenContent()
    }
}

@Composable
fun CameraScreenContent(
    viewModel: CameraViewModel = koinViewModel(),
) {
    val permissionState = rememberCameraPermissionState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collect { event ->
            snackbarHostState.showTyped(event.message, event.type)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
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

        AppSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@Composable
private fun CameraContent(viewModel: CameraViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    AnimatedContent(
        targetState = uiState,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        contentKey = { it::class },
    ) { state ->
    when (state) {
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
        is CameraUiState.Analyzing -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                LoadingContent(message = "AI 分析中...")
            }
        }
        is CameraUiState.Saved -> {
            val navigator = LocalNavigator.currentOrThrow
            val spacing = MaterialTheme.spacing
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(spacing.lg),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.md, Alignment.CenterVertically),
            ) {
                if (state.analysisResult != null) {
                    ScoreRing(
                        score = state.analysisResult.overallScore,
                        label = "综合评分",
                    )
                    Text(
                        text = state.analysisResult.summary,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = spacing.md),
                    )
                    // Mini metric row (5 dimensions)
                    MiniMetricRow(state.analysisResult)
                } else {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(spacing.section),
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
                if (state.milestoneMessage != null) {
                    // Streak badge - Apricot pill style
                    Row(
                        modifier = Modifier
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(Apricot50, Color(0xFFFFF8F0)),
                                ),
                                shape = RoundedCornerShape(percent = 50),
                            )
                            .padding(horizontal = spacing.lg, vertical = spacing.sm),
                        horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "\uD83D\uDD25", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = state.milestoneMessage,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Apricot300,
                        )
                    }
                }
                // Dual action buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = spacing.md),
                    horizontalArrangement = Arrangement.spacedBy(spacing.md),
                ) {
                    OutlinedButton(
                        onClick = viewModel::resetToPreview,
                        modifier = Modifier
                            .weight(1f)
                            .height(MaterialTheme.dimens.buttonHeight),
                        shape = RoundedCornerShape(percent = 50),
                    ) {
                        Text("继续拍照")
                    }
                    Button(
                        onClick = {
                            state.recordId?.let { id ->
                                navigator.push(RecordDetailScreen(id))
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(MaterialTheme.dimens.buttonHeight),
                        enabled = state.recordId != null,
                        shape = RoundedCornerShape(percent = 50),
                    ) {
                        Text("查看详情")
                    }
                }
            }
        }
        is CameraUiState.FeatureGated -> {
            val navigator = LocalNavigator.currentOrThrow
            RecordLimitShowcase(
                onUpgrade = { navigator.push(PaywallScreen()) },
            )
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
}

@Composable
private fun CaptureButton(
    onClick: () -> Unit,
    enabled: Boolean,
) {
    val cameraColors = MaterialTheme.extendedColors.camera
    val dimens = MaterialTheme.dimens
    Box(
        modifier = Modifier
            .size(dimens.captureButtonSize)
            .clip(CircleShape)
            .border(dimens.captureButtonBorder, cameraColors.buttonBorder, CircleShape)
            .padding(dimens.captureButtonInnerPadding)
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
    val spacing = MaterialTheme.spacing
    val navigator = LocalNavigator.currentOrThrow
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Large pink camera icon circle
        Box(
            modifier = Modifier
                .size(130.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Rose100, Rose50),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "\uD83D\uDCF7",
                style = MaterialTheme.typography.displayMedium,
            )
        }

        Spacer(modifier = Modifier.height(spacing.lg))

        Text(
            text = "来拍张自拍吧",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
        )

        Spacer(modifier = Modifier.height(spacing.sm))

        Text(
            text = "我们需要使用相机来帮你记录肌肤状态，\n所有照片仅保存在你的设备上。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = spacing.sm),
        )

        Spacer(modifier = Modifier.height(spacing.lg))

        // Feature list with colored dots
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
            modifier = Modifier.fillMaxWidth(),
        ) {
            FeatureBullet(
                text = "AI 智能分析五大肌肤指标",
                dotBg = Mint50,
                dotColor = MaterialTheme.colorScheme.primary,
            )
            FeatureBullet(
                text = "追踪每天的肌肤变化趋势",
                dotBg = Lavender50,
                dotColor = Lavender300,
            )
            FeatureBullet(
                text = "照片仅存储在本地设备中",
                dotBg = Rose50,
                dotColor = Rose400,
            )
        }

        Spacer(modifier = Modifier.height(spacing.xl))

        // Full-width primary button
        Button(
            onClick = onRequest,
            modifier = Modifier
                .fillMaxWidth()
                .height(MaterialTheme.dimens.buttonHeight),
            shape = RoundedCornerShape(percent = 50),
        ) {
            Text("允许使用相机")
        }

        // "下次再说" text button
        TextButton(
            onClick = { navigator.pop() },
            modifier = Modifier.padding(top = spacing.sm),
        ) {
            Text(
                text = "下次再说",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun FeatureBullet(
    text: String,
    dotBg: Color,
    dotColor: Color,
) {
    val spacing = MaterialTheme.spacing
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(dotBg),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = dotColor,
                modifier = Modifier.size(15.dp),
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun PermissionDeniedContent() {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(spacing.section),
            tint = MaterialTheme.colorScheme.error,
        )
        Spacer(modifier = Modifier.height(spacing.md))
        Text(
            text = "相机权限被拒绝",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(spacing.sm))
        Text(
            text = "请按以下步骤开启相机权限：",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(spacing.sm))
        Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
            Text(
                text = "1. 打开手机「设置」",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "2. 找到「SkinTrack」应用",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "3. 开启「相机」权限",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Spacer(modifier = Modifier.height(spacing.lg))
        Button(
            onClick = { /* TODO: open system settings */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(MaterialTheme.dimens.buttonHeight),
            shape = RoundedCornerShape(percent = 50),
        ) {
            Text("打开系统设置")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecordLimitShowcase(
    onUpgrade: () -> Unit,
) {
    val spacing = MaterialTheme.spacing
    val functional = MaterialTheme.extendedColors.functional

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.md, Alignment.CenterVertically),
    ) {
        // Record thumbnail placeholders (max 4 circles)
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            repeat(4) {
                Box(
                    modifier = Modifier
                        .size(MaterialTheme.dimens.thumbnailSize)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "${it + 1}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        // Progress summary
        Text(
            text = "\u2191+10\u5206",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = functional.success,
        )
        Text(
            text = "3次记录，评分提升10分",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        // Encouragement
        Text(
            text = "第4次记录能看到更完整的趋势",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        // Feature pills
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(
                spacing.sm,
                Alignment.CenterHorizontally,
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.xs),
            modifier = Modifier.fillMaxWidth(),
        ) {
            listOf("无限检测", "AI分析", "归因报告").forEach { tag ->
                Text(
                    text = tag,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.small,
                        )
                        .padding(
                            horizontal = spacing.sm,
                            vertical = spacing.xs,
                        ),
                )
            }
        }

        // Apricot gradient CTA button
        val buttonShape = MaterialTheme.shapes.large
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(MaterialTheme.dimens.buttonHeight)
                .clip(buttonShape)
                .background(
                    brush = Brush.horizontalGradient(listOf(Apricot300, Apricot100)),
                    shape = buttonShape,
                )
                .clickable(onClick = onUpgrade),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "\u2B50 升级Pro\u00B7继续你的旅程",
                color = Color.White,
                style = MaterialTheme.typography.labelLarge,
            )
        }

        // Trial hint
        Text(
            text = "新用户14天免费试用",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun MiniMetricRow(result: com.skintrack.app.data.remote.SkinAnalysisResult) {
    val spacing = MaterialTheme.spacing
    val skinMetric = MaterialTheme.extendedColors.skinMetric
    val metrics = listOf(
        Triple("痘痘", 100 - result.acneCount.coerceIn(0, 100), skinMetric.acne),
        Triple("毛孔", result.poreScore, skinMetric.pore),
        Triple("均匀", result.evenScore, skinMetric.evenness),
        Triple("泛红", result.rednessScore, skinMetric.redness),
        Triple("水润", 100 - result.blackheadDensity.coerceIn(0, 100), skinMetric.hydration),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.md),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        metrics.forEach { (label, score, color) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color),
                )
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
