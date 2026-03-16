package com.skintrack.app.ui.screen.report

import androidx.compose.foundation.background
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
                PhotoHeaderSection(record, onBack, onShare, state.scoreDiff)
            }

            // Radar chart
            item(key = "radar_chart") {
                RadarChartCard(
                    record,
                    modifier = Modifier.padding(horizontal = spacing.md),
                )
            }

            // Metric detail bars
            item(key = "metric_bars") {
                MetricBarsCard(
                    record,
                    modifier = Modifier.padding(
                        horizontal = spacing.md,
                        vertical = spacing.sm,
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
                            modifier = Modifier.padding(horizontal = spacing.md),
                        )
                    }
                }
            } else {
                item(key = "ai_locked") {
                    val navigator = LocalNavigator.currentOrThrow
                    LockedFeatureCard(
                        message = FeatureGate.DETAILED_AI_REPORT.lockedMessage,
                        onUpgrade = { navigator.push(PaywallScreen()) },
                        modifier = Modifier.padding(horizontal = spacing.md),
                    )
                }
            }

            // Daily products card
            item(key = "daily_products") {
                DailyProductsCard(
                    state.usedProducts,
                    modifier = Modifier.padding(
                        horizontal = spacing.md,
                        vertical = spacing.sm,
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
                        bottom = spacing.lg,
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
                        .padding(bottom = spacing.md)
                        .background(
                            color = Color.Black.copy(alpha = 0.35f),
                            shape = FullRoundedShape,
                        )
                        .padding(horizontal = spacing.md, vertical = spacing.sm),
                ) {
                    Text(
                        text = "${record.overallScore}分 · ${getScoreLabel(record.overallScore)}",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
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
                            .size(36.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.3f),
                                shape = CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "返回",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
                IconButton(onClick = onShare) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                color = Color.Black.copy(alpha = 0.3f),
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
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.lg, vertical = spacing.md),
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
                        text = "肌肤状态${getScoreLabel(record.overallScore)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        text = formatRecordDate(record.recordedAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    // Score change pill
                    if (scoreDiff != null) {
                        val (text, color) = when {
                            scoreDiff > 0 -> "↑ +$scoreDiff 较上次" to MaterialTheme.extendedColors.functional.success
                            scoreDiff < 0 -> "↓ $scoreDiff 较上次" to MaterialTheme.colorScheme.error
                            else -> "→ 0 较上次" to MaterialTheme.colorScheme.onSurfaceVariant
                        }
                        Text(
                            text = text,
                            style = MaterialTheme.typography.labelMedium,
                            color = color,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(
                                    color = color.copy(alpha = 0.1f),
                                    shape = FullRoundedShape,
                                )
                                .padding(horizontal = spacing.sm, vertical = spacing.xs),
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

    val metrics = buildList {
        record.overallScore?.let {
            add(RadarMetric("综合", it.toFloat(), color = MaterialTheme.colorScheme.primary))
        }
        record.poreScore?.let {
            add(RadarMetric("毛孔", it.toFloat(), color = skinMetric.pore))
        }
        record.rednessScore?.let {
            add(RadarMetric("泛红", it.toFloat(), color = skinMetric.redness))
        }
        record.evenScore?.let {
            add(RadarMetric("均匀度", it.toFloat(), color = skinMetric.evenness))
        }
        record.blackheadDensity?.let {
            add(RadarMetric("黑头", it.toFloat(), color = skinMetric.hydration))
        }
        record.acneCount?.let {
            // Normalize acne count to 0-100 scale (0 acne = 100 score, 20+ acne = 0 score)
            val normalized = (100f - it * 5f).coerceIn(0f, 100f)
            add(RadarMetric("痘痘", normalized, color = skinMetric.acne))
        }
    }

    if (metrics.size >= 3) {
        SectionCard(modifier = modifier) {
            SectionHeader("多维评分")
            RadarChart(
                metrics = metrics,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun MetricBarsCard(
    record: SkinRecord,
    modifier: Modifier = Modifier,
    scoreDiff: RecordDetailUiState.Content? = null,
) {
    val skinMetric = MaterialTheme.extendedColors.skinMetric

    SectionCard(modifier = modifier) {
        SectionHeader("各项指标")

        // Design spec: gradient bars with change values
        record.acneCount?.let { score ->
            val normalized = (100 - score * 5).coerceIn(0, 100)
            MetricBarRow(
                label = "痘痘",
                score = normalized,
                gradientStart = Color(0xFFFF8A8A),
                gradientEnd = skinMetric.acne,
            )
        }
        record.poreScore?.let { score ->
            MetricBarRow(
                label = "毛孔",
                score = score,
                gradientStart = Color(0xFF7EDCD4),
                gradientEnd = skinMetric.pore,
            )
        }
        record.evenScore?.let { score ->
            MetricBarRow(
                label = "均匀度",
                score = score,
                gradientStart = Color(0xFFFFEE8D),
                gradientEnd = skinMetric.evenness,
            )
        }
        record.rednessScore?.let { score ->
            MetricBarRow(
                label = "泛红",
                score = score,
                gradientStart = Color(0xFFF5A1A1),
                gradientEnd = skinMetric.redness,
            )
        }
        record.blackheadDensity?.let { score ->
            MetricBarRow(
                label = "水润",
                score = score,
                gradientStart = Color(0xFF75C7E1),
                gradientEnd = skinMetric.hydration,
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
            .padding(vertical = spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Label (52dp width per design)
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(52.dp),
        )

        // Gradient bar (7dp height per design)
        Box(
            modifier = Modifier
                .weight(1f)
                .height(7.dp)
                .clip(FullRoundedShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(7.dp)
                    .clip(FullRoundedShape)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(gradientStart, gradientEnd),
                        ),
                    ),
            )
        }

        // Score value
        Text(
            text = "$score",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold,
        )

        // Change value
        if (change != null) {
            val functional = MaterialTheme.extendedColors.functional
            val changeColor = when {
                change >= 3 -> functional.success
                change <= -1 -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            Text(
                text = if (change >= 0) "+$change" else "$change",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = changeColor,
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

    // Gradient background card matching design spec
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFF0FAF6),
                            Color(0xFFF5F0FF),
                        ),
                    ),
                )
                .padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            SectionHeader("AI 分析")

            // "AI 智能生成" pill badge
            Text(
                text = "AI 智能生成",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = FullRoundedShape,
                    )
                    .padding(horizontal = spacing.sm, vertical = spacing.xs),
            )

            if (summary != null) {
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 24.sp,
                )
            }

            if (recommendations.isNotEmpty()) {
                // Highlight box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0x142D9F7F),
                                    Color(0x0FA78BFA),
                                ),
                            ),
                            shape = MaterialTheme.shapes.small,
                        )
                        .padding(spacing.sm),
                    verticalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    recommendations.forEach { rec ->
                        Text(
                            text = rec,
                            style = MaterialTheme.typography.bodySmall,
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
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = FullRoundedShape,
                            )
                            .padding(horizontal = spacing.sm + spacing.xs, vertical = spacing.sm),
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
