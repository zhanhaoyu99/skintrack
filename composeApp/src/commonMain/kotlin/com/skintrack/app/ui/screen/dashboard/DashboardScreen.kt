package com.skintrack.app.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.ui.component.ScoreRing
import com.skintrack.app.ui.component.SectionCard
import com.skintrack.app.ui.component.SectionHeader
import com.skintrack.app.ui.component.animateFadeIn
import com.skintrack.app.ui.component.animateListItem
import com.skintrack.app.ui.screen.attribution.AttributionReportScreen
import com.skintrack.app.ui.screen.camera.CameraScreen
import com.skintrack.app.ui.screen.product.ProductManageScreen
import com.skintrack.app.ui.screen.share.ShareCardScreen
import com.skintrack.app.ui.screen.timeline.TrendChart
import com.skintrack.app.ui.theme.Apricot100
import com.skintrack.app.ui.theme.Apricot50
import com.skintrack.app.ui.theme.FullRoundedShape
import com.skintrack.app.ui.theme.Lavender50
import com.skintrack.app.ui.theme.Lavender100
import com.skintrack.app.ui.theme.Lavender300
import com.skintrack.app.ui.theme.Mint50
import com.skintrack.app.ui.theme.Mint100
import com.skintrack.app.ui.theme.Rose50
import com.skintrack.app.ui.theme.Rose100
import com.skintrack.app.ui.theme.Rose200
import com.skintrack.app.ui.theme.Rose300
import com.skintrack.app.ui.theme.Rose400
import com.skintrack.app.ui.theme.Rose500
import com.skintrack.app.ui.theme.Rose600
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.extendedColors
import com.skintrack.app.ui.theme.gradients
import com.skintrack.app.ui.theme.spacing
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.viewmodel.koinViewModel

// Dark-mode-aware accent helpers
@Composable private fun roseBackground() =
    if (isSystemInDarkTheme()) Rose300.copy(alpha = 0.12f) else Rose50

@Composable private fun roseBgEnd() =
    if (isSystemInDarkTheme()) Rose300.copy(alpha = 0.06f) else Color(0xFFFFF5F6)

@Composable private fun roseButton() =
    if (isSystemInDarkTheme()) Rose500 else Rose300

@Composable private fun lavenderBg() =
    if (isSystemInDarkTheme()) Lavender300.copy(alpha = 0.12f) else Lavender50

@Composable private fun roseBgLight() =
    if (isSystemInDarkTheme()) Rose300.copy(alpha = 0.1f) else Rose50

@Composable private fun avatarGradient() =
    if (isSystemInDarkTheme()) listOf(Rose500, Rose600) else listOf(Rose200, Rose300)

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedPeriod by viewModel.selectedTrendPeriod.collectAsState()
    val navigator = LocalNavigator.currentOrThrow

    when (val state = uiState) {
        DashboardUiState.Loading -> {
            DashboardLoadingSkeleton()
        }

        is DashboardUiState.Empty -> {
            DashboardEmptyContent(
                username = state.username,
                onTakePhoto = { navigator.push(CameraScreen()) },
            )
        }

        is DashboardUiState.Content -> {
            DashboardContent(
                state = state,
                selectedPeriod = selectedPeriod,
                onPeriodChange = viewModel::onTrendPeriodChange,
                onTakePhoto = { navigator.push(CameraScreen()) },
                onNavigateTimeline = { /* Tab switch handled by HomeScreen */ },
                onNavigateProduct = { navigator.push(ProductManageScreen()) },
                onNavigateAttribution = { navigator.push(AttributionReportScreen()) },
                onNavigateShare = { navigator.push(ShareCardScreen()) },
            )
        }
    }
}

// region Header

@Composable
private fun DashboardHeader(
    username: String,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            val hour = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault()).hour
            val greeting = when {
                hour < 6 -> "凌晨好"
                hour < 12 -> "上午好 \u2600\uFE0F"
                hour < 18 -> "下午好"
                else -> "晚上好 \uD83C\uDF19"
            }
            Text(
                text = greeting,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.1.sp,
            )
            Text(
                text = username,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.6).sp,
                modifier = Modifier.padding(top = 2.dp),
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Notification bell in surface circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        0.5.dp,
                        MaterialTheme.colorScheme.outlineVariant,
                        CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "通知",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                // Red dot indicator
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-9).dp, y = 9.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        .background(MaterialTheme.extendedColors.functional.error),
                )
            }

            // User avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(avatarGradient()),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = username.firstOrNull()?.uppercase() ?: "U",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }
        }
    }
}

// endregion

// region Empty State

@Composable
private fun DashboardEmptyContent(
    username: String,
    onTakePhoto: () -> Unit,
) {
    val spacing = MaterialTheme.spacing

    val roseBg = roseBackground()

    Column(modifier = Modifier.fillMaxSize()) {
        DashboardHeader(username = username)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    // Decorative gradient background circles
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Mint100.copy(alpha = 0.5f), Color.Transparent),
                        ),
                        radius = 110.dp.toPx(),
                        center = Offset(-40.dp.toPx(), size.height * 0.12f),
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(roseBg.copy(alpha = 0.4f), Color.Transparent),
                        ),
                        radius = 90.dp.toPx(),
                        center = Offset(size.width + 30.dp.toPx(), size.height * 0.7f),
                    )
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Lavender50.copy(alpha = 0.3f),
                                Color.Transparent,
                            ),
                        ),
                        radius = 50.dp.toPx(),
                        center = Offset(size.width * 0.8f, size.height * 0.3f),
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Hero illustration area
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .animateFadeIn(0),
                    contentAlignment = Alignment.Center,
                ) {
                    // Main gradient circle
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(Mint100, Mint50, MaterialTheme.colorScheme.surfaceVariant),
                                ),
                            ),
                    )
                    // Floating accent circles
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .offset(x = 60.dp, y = (-60).dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(listOf(Rose100, Rose50)),
                                alpha = 0.6f,
                            ),
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .offset(x = (-60).dp, y = 50.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.linearGradient(listOf(Lavender100, Lavender50)),
                                alpha = 0.5f,
                            ),
                    )
                    // Camera icon
                    Text(
                        text = "\uD83D\uDCF7",
                        fontSize = 48.sp,
                    )
                }

                Spacer(modifier = Modifier.height(spacing.xl))

                // Title
                Text(
                    text = "开启你的变美日记",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    letterSpacing = (-0.5).sp,
                    modifier = Modifier.animateFadeIn(100),
                )

                Spacer(modifier = Modifier.height(spacing.sm))

                // Subtitle
                Text(
                    text = "拍一张自拍，AI 帮你分析肌肤状态\n见证皮肤一天天变好~",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp,
                    modifier = Modifier.animateFadeIn(150),
                )

                Spacer(modifier = Modifier.height(spacing.xl))

                // 3-step guide — horizontal layout with colored border circles
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateFadeIn(200),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    EmptyStepItem(
                        emoji = "\uD83D\uDCF7",
                        label = "拍照",
                        borderColor = MaterialTheme.colorScheme.primary,
                        bgColor = Mint50,
                    )
                    EmptyStepItem(
                        emoji = "\uD83D\uDD2C",
                        label = "AI 分析",
                        borderColor = Lavender300,
                        bgColor = Lavender50,
                    )
                    EmptyStepItem(
                        emoji = "\uD83D\uDCC8",
                        label = "追踪",
                        borderColor = Rose300,
                        bgColor = roseBackground(),
                    )
                }

                Spacer(modifier = Modifier.height(spacing.xl))

                // CTA button
                Button(
                    onClick = onTakePhoto,
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(MaterialTheme.dimens.buttonHeight)
                        .animateFadeIn(300),
                    shape = MaterialTheme.shapes.extraLarge,
                ) {
                    Text(
                        text = "拍第一张自拍",
                        fontSize = 16.sp,
                    )
                }

                Spacer(modifier = Modifier.height(spacing.md))

                // Social proof
                Text(
                    text = buildAnnotatedString {
                        append("已有 ")
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            ),
                        ) {
                            append("12,580")
                        }
                        append(" 位用户在使用")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.animateFadeIn(400),
                )
            }
        }
    }
}

@Composable
private fun EmptyStepItem(
    emoji: String,
    label: String,
    borderColor: Color,
    bgColor: Color,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(bgColor)
                .border(1.5.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = emoji, fontSize = 16.sp)
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
        )
    }
}

// endregion

// region Content State

@Composable
private fun DashboardContent(
    state: DashboardUiState.Content,
    selectedPeriod: Int,
    onPeriodChange: (Int) -> Unit,
    onTakePhoto: () -> Unit,
    onNavigateTimeline: () -> Unit,
    onNavigateProduct: () -> Unit,
    onNavigateAttribution: () -> Unit,
    onNavigateShare: () -> Unit,
) {
    val spacing = MaterialTheme.spacing

    Column(modifier = Modifier.fillMaxSize()) {
        DashboardHeader(username = state.username)

        LazyColumn(
            contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // 1. Hero Card
            item(key = "hero") {
                HeroCard(
                    record = state.latestRecord,
                    scoreChange = state.scoreChange,
                    modifier = Modifier.animateListItem(0),
                )
            }

            // 2. Mini Metric Row
            item(key = "metrics") {
                MiniMetricRow(
                    record = state.latestRecord,
                    modifier = Modifier.animateListItem(1),
                )
            }

            // 3. Camera Reminder (if no photo today)
            if (!state.hasTakenPhotoToday) {
                item(key = "camera_reminder") {
                    CameraReminderCard(
                        onTakePhoto = onTakePhoto,
                        modifier = Modifier.animateListItem(2),
                    )
                }
            }

            // 4. Quick Actions Grid
            item(key = "quick_actions") {
                QuickActionsGrid(
                    onNavigateTimeline = onNavigateTimeline,
                    onNavigateProduct = onNavigateProduct,
                    onNavigateAttribution = onNavigateAttribution,
                    onNavigateShare = onNavigateShare,
                    modifier = Modifier.animateListItem(3),
                )
            }

            // 5. Skincare Tip
            item(key = "tip") {
                SkincareTipCard(
                    modifier = Modifier.animateListItem(4),
                )
            }

            // 6. Check-in Card
            item(key = "streak") {
                CheckInCard(
                    streak = state.currentStreak,
                    weekCheckIns = state.weekCheckIns,
                    modifier = Modifier.animateListItem(5),
                )
            }

            // 7. Trend Chart
            if (state.chartPoints.size >= 2) {
                item(key = "trend") {
                    TrendChartCard(
                        state = state,
                        selectedPeriod = selectedPeriod,
                        onPeriodChange = onPeriodChange,
                        modifier = Modifier.animateListItem(6),
                    )
                }
            }

            // Bottom spacing
            item { Spacer(modifier = Modifier.height(spacing.md)) }
        }
    }
}

// endregion

// region Hero Card

@Composable
private fun HeroCard(
    record: SkinRecord,
    scoreChange: Float,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = MaterialTheme.gradients.hero),
        ) {
            // Decorative circles
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .offset(x = 100.dp, y = (-50).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f)),
            )
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .offset(x = (-30).dp, y = 80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f)),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
            ) {
                // Top: ScoreRing + status text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // ScoreRing
                    if (record.overallScore != null) {
                        ScoreRing(
                            score = record.overallScore,
                            label = "总评分",
                            size = 84.dp,
                            strokeWidth = 6.5.dp,
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Text(
                            text = getScoreStatusTitle(record.overallScore),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 19.sp,
                            letterSpacing = (-0.3).sp,
                        )
                        Text(
                            text = getScoreStatusSubtitle(record.overallScore),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.72f),
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                        )
                    }
                }

                // Bottom: trend pill + date
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Trend pill
                    if (scoreChange != 0f) {
                        val isPositive = scoreChange > 0
                        val arrow = if (isPositive) "↑" else "↓"
                        val sign = if (isPositive) "+" else ""

                        Box(
                            modifier = Modifier
                                .clip(FullRoundedShape)
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                        ) {
                            Text(
                                text = "$arrow 较上周 $sign${scoreChange.let { "%.1f".format(it) }}",
                                style = MaterialTheme.typography.labelMedium,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                            )
                        }
                    }

                    // Record date
                    val recordDate = record.recordedAt
                        .toLocalDateTime(TimeZone.currentSystemDefault()).date
                    Text(
                        text = "${recordDate.year}.${recordDate.monthNumber.toString().padStart(2, '0')}.${recordDate.dayOfMonth.toString().padStart(2, '0')} 记录",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.55f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}

private fun getScoreStatusTitle(score: Int?): String = when {
    score == null -> "等待评分"
    score >= 85 -> "皮肤状态超棒"
    score >= 70 -> "皮肤状态不错哦"
    score >= 55 -> "状态还行"
    score >= 40 -> "需要关注"
    else -> "需要改善"
}

private fun getScoreStatusSubtitle(score: Int?): String = when {
    score == null -> "拍照后即可获得评分"
    score >= 85 -> "各项指标都很优秀，继续保持哦~"
    score >= 70 -> "各项指标都在稳步改善中，继续保持~"
    score >= 55 -> "部分指标有改善空间，加油~"
    score >= 40 -> "建议调整护肤方案，关注重点指标"
    else -> "别灰心，持续记录会看到改善的~"
}

// endregion

// region Mini Metric Row

@Composable
private fun MiniMetricRow(
    record: SkinRecord,
    modifier: Modifier = Modifier,
) {
    val skinMetric = MaterialTheme.extendedColors.skinMetric
    val spacing = MaterialTheme.spacing

    data class MetricItem(
        val label: String,
        val score: Int?,
        val color: Color,
    )

    val metrics = listOf(
        MetricItem("痘痘", record.acneCount?.let { (100 - it * 5).coerceIn(0, 100) }, skinMetric.acne),
        MetricItem("毛孔", record.poreScore, skinMetric.pore),
        MetricItem("均匀", record.evenScore, skinMetric.evenness),
        MetricItem("泛红", record.rednessScore, skinMetric.redness),
        MetricItem("水润", record.blackheadDensity?.let { (100 - it * 5).coerceIn(0, 100) }, skinMetric.hydration),
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        metrics.forEach { metric ->
            MiniMetricCard(
                label = metric.label,
                score = metric.score,
                color = metric.color,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MiniMetricCard(
    label: String,
    score: Int?,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Score value (colored)
            Text(
                text = score?.toString() ?: "--",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color,
                lineHeight = 16.sp,
            )

            // Label
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )

            // Mini bar
            LinearProgressIndicator(
                progress = { (score ?: 0) / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .height(3.dp)
                    .clip(FullRoundedShape),
                color = color,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}

// endregion

// region Camera Reminder Card

@Composable
private fun CameraReminderCard(
    onTakePhoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(listOf(roseBackground(), roseBgEnd())),
                )
                .padding(horizontal = spacing.md, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Camera icon box
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                if (isSystemInDarkTheme()) Rose300.copy(alpha = 0.2f) else Rose100,
                                if (isSystemInDarkTheme()) Rose300.copy(alpha = 0.1f) else Rose50,
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "\uD83D\uDCF7",
                    fontSize = 22.sp,
                )
            }

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = "今天还没拍照哦~",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "坚持记录，看见皮肤的变化",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }

            Button(
                onClick = onTakePhoto,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isSystemInDarkTheme()) Rose500 else Rose400,
                ),
                shape = FullRoundedShape,
            ) {
                Text(
                    text = "去拍照",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

// endregion

// region Quick Actions Grid

@Composable
private fun QuickActionsGrid(
    onNavigateTimeline: () -> Unit,
    onNavigateProduct: () -> Unit,
    onNavigateAttribution: () -> Unit,
    onNavigateShare: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            QuickActionCard(
                title = "肌肤趋势",
                subtitle = "7 天变化",
                iconEmoji = "\uD83D\uDCC8",
                iconBgColor = Mint100,
                onClick = onNavigateTimeline,
                modifier = Modifier.weight(1f),
            )
            QuickActionCard(
                title = "护肤品",
                subtitle = "在用产品",
                iconEmoji = "\uD83E\uDDF4",
                iconBgColor = Apricot100,
                onClick = onNavigateProduct,
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            QuickActionCard(
                title = "归因分析",
                subtitle = "AI 洞察",
                iconEmoji = "\uD83D\uDD2C",
                iconBgColor = lavenderBg(),
                onClick = onNavigateAttribution,
                modifier = Modifier.weight(1f),
            )
            QuickActionCard(
                title = "分享对比",
                subtitle = "记录蜕变",
                iconEmoji = "\uD83D\uDCE4",
                iconBgColor = roseBgLight(),
                onClick = onNavigateShare,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    iconEmoji: String,
    iconBgColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Colored icon with rounded square
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = iconEmoji,
                    fontSize = 20.sp,
                )
            }

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 1.dp),
                )
            }
        }
    }
}

// endregion

// region Skincare Tip Card

@Composable
private fun SkincareTipCard(
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    data class TipItem(val title: String, val subtitle: String)

    val tips = listOf(
        TipItem("今日紫外线偏强", "记得涂防晒哦~ 出门前 15 分钟使用效果最佳"),
        TipItem("换季护肤提醒", "注意精简护肤步骤，减少皮肤负担"),
        TipItem("睡前小建议", "护肤后等3分钟再上枕头，让产品充分吸收"),
        TipItem("去角质提醒", "每周至少做一次温和去角质，帮助后续吸收"),
        TipItem("面膜时间", "敷面膜不宜超过15分钟，否则反而会倒吸水分"),
        TipItem("温和洁面", "早晨洁面用温水即可，过度清洁会破坏皮脂膜"),
        TipItem("防晒用量", "涂防晒需要硬币大小的量才能达到标注的SPF值"),
        TipItem("早C晚A", "维C精华建议早上用，维A醇建议晚上用"),
        TipItem("保质期提醒", "护肤品打开后注意保质期，一般6-12个月"),
        TipItem("美容觉", "充足睡眠是最好的护肤品，尽量在23点前入睡"),
    )
    val dayOfYear = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).dayOfYear
    val tip = tips[dayOfYear % tips.size]

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(listOf(lavenderBg(), roseBgLight())),
                )
                .padding(horizontal = spacing.md, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Icon box
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                if (isSystemInDarkTheme()) Lavender300.copy(alpha = 0.2f) else Lavender100,
                                if (isSystemInDarkTheme()) Lavender300.copy(alpha = 0.1f) else Lavender50,
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "\u2600\uFE0F", fontSize = 20.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tip.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = tip.subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
    }
}

// endregion

// region Check-in Card

@Composable
private fun CheckInCard(
    streak: Int,
    weekCheckIns: List<DayCheckIn>,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    SectionCard(modifier = modifier) {
        // Title row: "每日打卡" left, "🔥连续 N 天" right
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "每日打卡",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(text = "\uD83D\uDD25", fontSize = 16.sp)
                Text(
                    text = "连续 $streak 天",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSystemInDarkTheme()) Rose400 else Rose400,
                )
            }
        }

        // Weekly calendar grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            weekCheckIns.forEach { day ->
                DayCircle(day = day)
            }
        }

        // Milestone badge
        if (streak >= 7) {
            val milestoneMessage = when {
                streak >= 30 -> "\uD83C\uDFC6 坚持一个月! 连续打卡 $streak 天，达成月度成就"
                streak >= 14 -> "\uD83C\uDF1F 太厉害了! 连续打卡 $streak 天，达成两周成就"
                else -> "\u2B50 太棒了! 连续打卡 $streak 天，达成一周成就"
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Mint50, Apricot50),
                        ),
                    )
                    .padding(horizontal = 12.dp, vertical = spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = milestoneMessage,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun DayCircle(day: DayCheckIn) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            text = day.weekdayLabel,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Box(
            modifier = Modifier
                .size(34.dp)
                .then(
                    when {
                        day.isCompleted -> Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                        day.isToday -> Modifier
                            .clip(CircleShape)
                            .background(Mint50)
                            .border(2.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        else -> Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (day.isCompleted) {
                Text(
                    text = "\u2713",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                )
            } else {
                Text(
                    text = "${day.dayOfMonth}",
                    fontSize = 12.sp,
                    color = if (day.isToday) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = if (day.isToday) FontWeight.ExtraBold else FontWeight.Bold,
                )
            }
        }
    }
}

data class DayCheckIn(
    val weekdayLabel: String,
    val dayOfMonth: Int,
    val isCompleted: Boolean,
    val isToday: Boolean,
)

// endregion

// region Trend Chart Card

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrendChartCard(
    state: DashboardUiState.Content,
    selectedPeriod: Int,
    onPeriodChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    val periods = listOf(7 to "7天", 30 to "30天", 90 to "90天")

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        SectionHeader(title = "肌肤趋势")

        // Period chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            periods.forEach { (days, label) ->
                FilterChip(
                    selected = selectedPeriod == days,
                    onClick = { onPeriodChange(days) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                )
            }
        }

        // Chart
        TrendChart(
            points = state.chartPoints,
            modifier = Modifier.fillMaxWidth(),
        )

        // Latest value annotation
        if (state.chartPoints.isNotEmpty()) {
            val latestScore = state.chartPoints.last().overallScore
            Text(
                text = "最新评分: $latestScore",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}

// endregion
