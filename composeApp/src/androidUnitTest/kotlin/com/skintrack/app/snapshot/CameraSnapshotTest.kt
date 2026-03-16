package com.skintrack.app.snapshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.skintrack.app.ui.component.ScoreRing
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
import org.junit.Test

class CameraSnapshotTest : SnapshotTestBase() {

    @Test
    fun camera_permission_request_light() = captureLight {
        CameraPermissionRequestPreview()
    }

    @Test
    fun camera_permission_request_dark() = captureDark {
        CameraPermissionRequestPreview()
    }

    @Test
    fun camera_permission_denied_light() = captureLight {
        CameraPermissionDeniedPreview()
    }

    @Test
    fun camera_permission_denied_dark() = captureDark {
        CameraPermissionDeniedPreview()
    }

    @Test
    fun camera_saved_with_analysis_light() = captureLight {
        CameraSavedWithAnalysisPreview()
    }

    @Test
    fun camera_saved_with_analysis_dark() = captureDark {
        CameraSavedWithAnalysisPreview()
    }

    @Test
    fun camera_saved_no_analysis_light() = captureLight {
        CameraSavedNoAnalysisPreview()
    }

    @Test
    fun camera_saved_no_analysis_dark() = captureDark {
        CameraSavedNoAnalysisPreview()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraPermissionRequestPreview() {
    val spacing = MaterialTheme.spacing
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("拍照记录") },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                textAlign = TextAlign.Center,
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
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.dimens.buttonHeight),
                shape = RoundedCornerShape(percent = 50),
            ) {
                Text("允许使用相机")
            }

            // "下次再说" text button
            TextButton(
                onClick = {},
                modifier = Modifier.padding(top = spacing.sm),
            ) {
                Text(
                    text = "下次再说",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraPermissionDeniedPreview() {
    val spacing = MaterialTheme.spacing
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("拍照记录") },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(spacing.section),
                    tint = MaterialTheme.colorScheme.error,
                )
                Text(
                    text = "相机权限被拒绝",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = "请按以下步骤开启相机权限：",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Column(verticalArrangement = Arrangement.spacedBy(spacing.xs)) {
                    Text("1. 打开手机「设置」", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("2. 找到「SkinTrack」应用", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("3. 开启「相机」权限", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.xl)
                        .height(MaterialTheme.dimens.buttonHeight),
                    shape = RoundedCornerShape(percent = 50),
                ) {
                    Text("打开系统设置")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraSavedWithAnalysisPreview() {
    val spacing = MaterialTheme.spacing
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("拍照记录") },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = spacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(spacing.md, Alignment.CenterVertically),
        ) {
            ScoreRing(score = 82, label = "综合评分")

            Text(
                text = "皮肤状态很棒哦!",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
            )
            Text(
                text = "皮肤整体状态良好，注意保湿和防晒",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            // Mini metric row with colored dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                val skinMetric = MaterialTheme.extendedColors.skinMetric
                listOf(
                    Triple("痘痘", "85", skinMetric.acne),
                    Triple("毛孔", "78", skinMetric.pore),
                    Triple("均匀", "82", skinMetric.evenness),
                    Triple("泛红", "75", skinMetric.redness),
                    Triple("水润", "80", skinMetric.hydration),
                ).forEach { (label, score, color) ->
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
                            text = score,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                Text(
                    text = "\uD83D\uDD25",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "连续打卡 5 天",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Apricot300,
                )
            }

            // Dual action buttons - full width
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(MaterialTheme.dimens.buttonHeight),
                    shape = RoundedCornerShape(percent = 50),
                ) {
                    Text("继续拍照")
                }
                Button(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(MaterialTheme.dimens.buttonHeight),
                    shape = RoundedCornerShape(percent = 50),
                ) {
                    Text("查看详情")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraSavedNoAnalysisPreview() {
    val spacing = MaterialTheme.spacing
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("拍照记录") },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                    }
                },
            )
        },
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = spacing.lg, end = spacing.lg, top = spacing.md),
                    horizontalArrangement = Arrangement.spacedBy(spacing.md),
                ) {
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier
                            .weight(1f)
                            .height(MaterialTheme.dimens.buttonHeight),
                        shape = RoundedCornerShape(percent = 50),
                    ) {
                        Text("继续拍照")
                    }
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .weight(1f)
                            .height(MaterialTheme.dimens.buttonHeight),
                        shape = RoundedCornerShape(percent = 50),
                    ) {
                        Text("查看详情")
                    }
                }
            }
        }
    }
}
