package com.skintrack.app.ui.screen.report

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.skintrack.app.domain.model.FeatureGate
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.domain.model.SkincareProduct
import com.skintrack.app.domain.model.displayName
import com.skintrack.app.domain.model.lockedMessage
import com.skintrack.app.platform.pathToImageModel
import com.skintrack.app.ui.component.LockedFeatureCard
import com.skintrack.app.ui.component.RadarChart
import com.skintrack.app.ui.component.RadarMetric
import com.skintrack.app.ui.component.ScoreRing
import com.skintrack.app.ui.component.SectionCard
import com.skintrack.app.ui.component.SectionHeader
import com.skintrack.app.ui.screen.paywall.PaywallScreen
import com.skintrack.app.ui.screen.share.ShareCardScreen
import com.skintrack.app.ui.screen.timeline.formatRecordDate
import com.skintrack.app.ui.theme.FullRoundedShape
import com.skintrack.app.ui.theme.Lavender300
import com.skintrack.app.ui.theme.Rose300
import com.skintrack.app.ui.theme.extendedColors
import com.skintrack.app.ui.theme.spacing
import org.koin.compose.viewmodel.koinViewModel

data class RecordDetailScreen(val recordId: String) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: RecordDetailViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(recordId) { viewModel.loadRecord(recordId) }

        when (val state = uiState) {
            is RecordDetailUiState.Loading -> {
                RecordDetailLoadingSkeleton()
            }
            is RecordDetailUiState.NotFound -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "未找到该记录",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            is RecordDetailUiState.Content -> {
                DetailContent(
                    state = state,
                    onBack = { navigator.pop() },
                    onShare = {
                        navigator.push(ShareCardScreen())
                    },
                )
            }
        }
    }
}

private val PhotoPreviewHeight = 280.dp
private val FloatingCardOverlap = 40.dp

private val SkinToneGradient = Brush.linearGradient(
    colors = listOf(
        Color(0xFFD4B8A8),
        Color(0xFFC8A898),
        Color(0xFFB8A088),
        Color(0xFFA89480),
    ),
)

private fun getScoreLabel(score: Int): String = when {
    score >= 85 -> "极佳"
    score >= 70 -> "良好"
    score >= 55 -> "一般"
    score >= 40 -> "较差"
    else -> "需改善"
}

private fun getScoreMessage(score: Int): String = when {
    score >= 85 -> "你的肌肤状态太棒了"
    score >= 70 -> "你的肌肤状态很棒哦"
    score >= 55 -> "你的肌肤状态还不错"
    score >= 40 -> "你的肌肤需要更多关注"
    else -> "让我们一起改善肌肤吧"
}

@Composable
private fun DetailContent(
    state: RecordDetailUiState.Content,
    onBack: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    val record = state.record

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            // Photo preview area + floating score card
            item(key = "photo_header") {
                PhotoHeaderSection(record, onBack, onShare, state.scoreDiff, state.percentile)
            }

            // Radar chart
            item(key = "radar_chart") {
                RadarChartCard(
                    record,
                    modifier = Modifier.padding(
                        horizontal = spacing.md,
                        vertical = spacing.iconGap,
                    ),
                )
            }

            // Metric detail bars
            item(key = "metric_bars") {
                MetricBarsCard(
                    record = record,
                    metricDiffs = state.metricDiffs,
                    modifier = Modifier.padding(
                        horizontal = spacing.md,
                        vertical = spacing.iconGap,
                    ),
                )
            }

            // AI summary card
            if (state.isPremium) {
                if (state.summary != null || state.recommendations.isNotEmpty()) {
                    item(key = "ai_summary") {
                        AiSummaryCard(
                            state.summary,
                            state.recommendations,
                            modifier = Modifier.padding(
                                horizontal = spacing.md,
                                vertical = spacing.iconGap,
                            ),
                        )
                    }
                }
            } else {
                item(key = "ai_locked") {
                    val navigator = LocalNavigator.currentOrThrow
                    LockedFeatureCard(
                        message = FeatureGate.DETAILED_AI_REPORT.lockedMessage,
                        onUpgrade = { navigator.push(PaywallScreen()) },
                        modifier = Modifier.padding(
                            horizontal = spacing.md,
                            vertical = spacing.iconGap,
                        ),
                        title = "解锁完整分析报告",
                        subtitle = "AI 智能分析 · 多维雷达图 · 个性化建议",
                    )
                }
            }

            // Daily products card
            item(key = "daily_products") {
                DailyProductsCard(
                    state.usedProducts,
                    modifier = Modifier.padding(
                        horizontal = spacing.md,
                        vertical = spacing.iconGap,
                    ),
                )
            }

            // Record info
            item(key = "record_info") {
                RecordInfoCard(
                    record,
                    modifier = Modifier.padding(
                        start = spacing.md,
                        end = spacing.md,
                        top = spacing.iconGap,
                        bottom = spacing.xl,
                    ),
                )
            }
        }
    }
}

@Composable
private fun PhotoHeaderSection(
    record: SkinRecord,
    onBack: () -> Unit,
    onShare: () -> Unit,
    scoreDiff: Int? = null,
    percentile: Int? = null,
) {
    val spacing = MaterialTheme.spacing

    Box(modifier = Modifier.fillMaxWidth()) {
        // Photo preview area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(PhotoPreviewHeight)
                .background(SkinToneGradient),
        ) {
            val imagePath = record.localImagePath
            if (imagePath != null) {
                AsyncImage(
                    model = pathToImageModel(imagePath),
                    contentDescription = "皮肤照片",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }

            // Score badge at bottom-center of photo (blur pill)
            if (record.overallScore != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = spacing.listGap)
                        .background(
                            color = Color.Black.copy(alpha = 0.35f),
                            shape = FullRoundedShape,
                        )
                        .padding(horizontal = spacing.listGap, vertical = spacing.xs),
                ) {
                    Text(
                        text = "${record.overallScore}分 · ${getScoreLabel(record.overallScore)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.2.sp,
                        color = Color.White,
                    )
                }
            }

            // Floating top bar (back + share)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = spacing.xs, vertical = spacing.xs),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBack) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.2f),
                                shape = CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp),
                        )
                    }
                }
                IconButton(onClick = onShare) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.2f),
                                shape = CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "\uD83D\uDCE4",
                            fontSize = 16.sp,
                        )
                    }
                }
            }
        }

        // Floating score overview card
        FloatingScoreCard(
            record = record,
            scoreDiff = scoreDiff,
            percentile = percentile,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md)
                .offset(y = PhotoPreviewHeight - FloatingCardOverlap)
                .zIndex(1f),
        )
    }
}

@Composable
private fun FloatingScoreCard(
    record: SkinRecord,
    scoreDiff: Int? = null,
    percentile: Int? = null,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.cardInner),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (record.overallScore != null) {
                ScoreRing(
                    score = record.overallScore,
                    size = 74.dp,
                    strokeWidth = 5.dp,
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    Text(
                        text = getScoreMessage(record.overallScore),
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.2).sp,
                    )
                    if (percentile != null) {
                        Text(
                            text = "整体评分超过 ${percentile}% 的用户",
                            fontSize = 13.sp,
                            letterSpacing = 0.05.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    } else {
                        Text(
                            text = formatRecordDate(record.recordedAt),
                            fontSize = 13.sp,
                            letterSpacing = 0.05.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    // Score change pill
                    if (scoreDiff != null) {
                        val (text, color) = when {
                            scoreDiff > 0 -> "↑ +$scoreDiff 较上次" to MaterialTheme.extendedColors.functional.success
                            scoreDiff < 0 -> "↓ $scoreDiff 较上次" to MaterialTheme.colorScheme.error
                            else -> "→ 0 较上次" to MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        Text(
                            text = text,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.2.sp,
                            color = color,
                            modifier = Modifier
                                .background(
                                    color = color.copy(alpha = 0.1f),
                                    shape = FullRoundedShape,
                                )
                                .padding(horizontal = spacing.compact, vertical = spacing.xxs),
                        )
                    }
                }
            } else {
                Text(
                    text = "--",
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = formatRecordDate(record.recordedAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun RadarChartCard(record: SkinRecord, modifier: Modifier = Modifier) {
    val skinMetric = MaterialTheme.extendedColors.skinMetric
    val hasMetrics = record.poreScore != null || record.rednessScore != null ||
        record.evenScore != null || record.blackheadDensity != null

    if (!hasMetrics) return

    // Order matches design spec: 综合→毛孔→泛红→水润→均匀→痘痘 (clockwise)
    val metrics = buildList {
        record.overallScore?.let {
            add(RadarMetric("整体", it.toFloat(), color = MaterialTheme.colorScheme.primary))
        }
        record.poreScore?.let {
            add(RadarMetric("毛孔", it.toFloat(), color = skinMetric.pore))
        }
        record.rednessScore?.let {
            add(RadarMetric("泛红", it.toFloat(), color = skinMetric.redness))
        }
        record.blackheadDensity?.let {
            add(RadarMetric("水润", it.toFloat(), color = skinMetric.hydration))
        }
        record.evenScore?.let {
            add(RadarMetric("均匀度", it.toFloat(), color = skinMetric.evenness))
        }
        record.acneCount?.let {
            // Normalize acne count to 0-100 scale (0 acne = 100 score, 20+ acne = 0 score)
            val normalized = (100f - it * 5f).coerceIn(0f, 100f)
            add(RadarMetric("痘痘", normalized, color = skinMetric.acne))
        }
    }

    if (metrics.size >= 3) {
        // Radial gradient fill: teal center → mint middle → lavender edge
        val radarFillBrush = Brush.radialGradient(
            colors = listOf(
                Color(0x1F4ECDC4), // teal at 0.12 opacity
                Color(0x1F2D9F7F), // mint/primary
                Color(0x1FA78BFA), // lavender
            ),
        )

        SectionCard(modifier = modifier) {
            SectionHeader("多维评分")
            RadarChart(
                metrics = metrics,
                fillBrush = radarFillBrush,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun MetricBarsCard(
    record: SkinRecord,
    metricDiffs: RecordDetailUiState.MetricDiffs = RecordDetailUiState.MetricDiffs(),
    modifier: Modifier = Modifier,
) {
    val skinMetric = MaterialTheme.extendedColors.skinMetric

    // Collect metric rows to add dividers between them
    data class MetricRow(val label: String, val score: Int, val gradientStart: Color, val gradientEnd: Color, val change: Int?)
    val metricRows = buildList {
        record.acneCount?.let { score ->
            val normalized = (100 - score * 5).coerceIn(0, 100)
            add(MetricRow("痘痘", normalized, Color(0xFFFF8A8A), skinMetric.acne, metricDiffs.acne))
        }
        record.poreScore?.let { score ->
            add(MetricRow("毛孔", score, Color(0xFF7EDCD4), skinMetric.pore, metricDiffs.pore))
        }
        record.evenScore?.let { score ->
            add(MetricRow("均匀度", score, Color(0xFFFFEE8D), skinMetric.evenness, metricDiffs.evenness))
        }
        record.rednessScore?.let { score ->
            add(MetricRow("泛红", score, Color(0xFFF5A1A1), skinMetric.redness, metricDiffs.redness))
        }
        record.blackheadDensity?.let { score ->
            add(MetricRow("水润", score, Color(0xFF75C7E1), skinMetric.hydration, metricDiffs.hydration))
        }
    }

    SectionCard(modifier = modifier) {
        SectionHeader("各项指标")

        metricRows.forEachIndexed { index, row ->
            if (index > 0) {
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outlineVariant,
                    thickness = 0.5.dp,
                )
            }
            MetricBarRow(
                label = row.label,
                score = row.score,
                gradientStart = row.gradientStart,
                gradientEnd = row.gradientEnd,
                change = row.change,
            )
        }
    }
}

@Composable
private fun MetricBarRow(
    label: String,
    score: Int,
    gradientStart: Color,
    gradientEnd: Color,
    change: Int? = null,
    maxScore: Int = 100,
) {
    val spacing = MaterialTheme.spacing
    val targetFraction = (score.toFloat() / maxScore).coerceIn(0f, 1f)
    val fraction by androidx.compose.animation.core.animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = androidx.compose.animation.core.tween(
            durationMillis = com.skintrack.app.ui.theme.Motion.LONG,
            easing = com.skintrack.app.ui.theme.Motion.EmphasizedDecelerate,
        ),
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacing.compact),
        horizontalArrangement = Arrangement.spacedBy(spacing.compact),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Label (36dp width, b3: 13sp/600)
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.05.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(36.dp),
        )

        // Gradient bar (8dp height per design) with micro glow shadow
        Box(
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .shadow(elevation = 1.dp, shape = FullRoundedShape)
                .clip(FullRoundedShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(8.dp)
                    .clip(FullRoundedShape)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(gradientStart, gradientEnd),
                        ),
                    ),
            )
        }

        // Score value (num-sm: 14sp/700)
        Text(
            text = "$score",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(28.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.End,
        )

        // Change value (c1: 12sp/600, 36dp width)
        if (change != null) {
            val functional = MaterialTheme.extendedColors.functional
            val changeColor = when {
                change >= 3 -> functional.success
                change <= -1 -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            Text(
                text = if (change >= 0) "+$change" else "$change",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.2.sp,
                color = changeColor,
                modifier = Modifier.width(36.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.End,
            )
        }
    }
}

@Composable
private fun AiSummaryCard(
    summary: String?,
    recommendations: List<String>,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    // Gradient background card matching design spec (dark-mode-aware)
    val isDark = isSystemInDarkTheme()
    val aiCardGradient = if (isDark) {
        Brush.linearGradient(
            listOf(
                Color(0xFF153E32).copy(alpha = 0.5f),
                Color(0xFF1E1830).copy(alpha = 0.3f),
            ),
        )
    } else {
        Brush.linearGradient(
            listOf(
                Color(0xFFF0FAF6),
                Color(0xFFF5F0FF),
            ),
        )
    }
    val aiHighlightGradient = if (isDark) {
        Brush.linearGradient(
            listOf(
                Color(0xFF58CAA5).copy(alpha = 0.06f),
                Color(0xFF58CAA5).copy(alpha = 0.06f),
            ),
        )
    } else {
        Brush.linearGradient(
            listOf(
                Color(0x142D9F7F),
                Color(0x0FA78BFA),
            ),
        )
    }

    val accentGradient = Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            Lavender300,
            Rose300,
        ),
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = aiCardGradient)
                .drawBehind {
                    // 3-color accent line at top (primary → lavender → rose)
                    val inset = 16.dp.toPx()
                    drawRect(
                        brush = accentGradient,
                        topLeft = androidx.compose.ui.geometry.Offset(inset, 0f),
                        size = androidx.compose.ui.geometry.Size(
                            width = size.width - inset * 2,
                            height = 2.dp.toPx(),
                        ),
                    )
                }
                .padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.listGap),
        ) {
            SectionHeader("AI 分析")

            // "AI 智能生成" pill badge (c2: 10sp/600)
            Text(
                text = "AI 智能生成",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.3.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = FullRoundedShape,
                    )
                    .padding(horizontal = spacing.compact, vertical = spacing.xxs),
            )

            if (summary != null) {
                // b2: 14sp/400/0.05, line-height 1.6 = ~22.4sp
                Text(
                    text = summary,
                    fontSize = 14.sp,
                    letterSpacing = 0.05.sp,
                    lineHeight = 22.sp,
                )
            }

            if (recommendations.isNotEmpty()) {
                // Highlight box (10px 12px padding, radius-sm)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = aiHighlightGradient,
                            shape = MaterialTheme.shapes.small,
                        )
                        .padding(horizontal = spacing.listGap, vertical = spacing.compact),
                    verticalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    recommendations.forEach { rec ->
                        // b3: 13sp/400/0.05
                        Text(
                            text = rec,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.05.sp,
                            lineHeight = 20.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DailyProductsCard(
    products: List<SkincareProduct>,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    SectionCard(modifier = modifier) {
        SectionHeader(
            title = "当日使用产品",
            trailing = {
                Text(
                    text = "编辑",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            },
        )

        if (products.isEmpty()) {
            Text(
                text = "当日无使用记录",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            // FlowRow-style wrapping chips
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                products.forEach { product ->
                    // c1: 12sp/500/0.2
                    Text(
                        text = product.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.2.sp,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = FullRoundedShape,
                            )
                            .padding(horizontal = spacing.listGap, vertical = spacing.iconGap),
                    )
                }
            }
        }
    }
}

@Composable
private fun RecordInfoCard(record: SkinRecord, modifier: Modifier = Modifier) {
    SectionCard(modifier = modifier) {
        SectionHeader("记录信息")
        Text(
            text = "拍摄时间: ${formatRecordDate(record.recordedAt)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "肤质类型: ${record.skinType.displayName}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
