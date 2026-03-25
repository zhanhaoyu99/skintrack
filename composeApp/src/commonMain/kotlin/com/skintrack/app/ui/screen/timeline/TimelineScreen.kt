package com.skintrack.app.ui.screen.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.domain.model.displayName
import com.skintrack.app.platform.pathToImageModel
import com.skintrack.app.ui.component.EmptyContent
import com.skintrack.app.ui.component.LoadingContent
import com.skintrack.app.ui.component.ScoreRing
import com.skintrack.app.ui.component.SectionHeader
import com.skintrack.app.ui.component.animateListItem
import com.skintrack.app.ui.screen.attribution.AttributionReportScreen
import com.skintrack.app.ui.screen.camera.CameraScreen
import com.skintrack.app.ui.screen.report.RecordDetailScreen
import com.skintrack.app.ui.screen.share.ShareCardScreen
import com.skintrack.app.ui.theme.FullRoundedShape
import com.skintrack.app.ui.theme.Lavender300
import com.skintrack.app.ui.theme.Primary300
import com.skintrack.app.ui.theme.Rose300
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.extendedColors
import com.skintrack.app.ui.theme.spacing
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val selectedMetric by viewModel.selectedMetric.collectAsState()
    val navigator = LocalNavigator.currentOrThrow

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "肌肤记录",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.md,
                vertical = MaterialTheme.spacing.sm,
            ),
        )

        // Time filter chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.md),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.iconGap),
        ) {
            items(TimelineFilter.entries) { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { viewModel.setFilter(filter) },
                    label = { Text(filter.label) },
                )
            }
        }

        when (val state = uiState) {
            is TimelineUiState.Loading -> {
                TimelineLoadingSkeleton(modifier = Modifier.fillMaxSize())
            }
            is TimelineUiState.Empty -> {
                TimelineEmptyState(
                    onStartCamera = { navigator.push(CameraScreen()) },
                    modifier = Modifier.fillMaxSize(),
                )
            }
            is TimelineUiState.Content -> {
                LazyColumn(
                    contentPadding = PaddingValues(
                        horizontal = MaterialTheme.spacing.md,
                        vertical = MaterialTheme.spacing.sm,
                    ),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.listGap),
                ) {
                    // 1. Compare card (design: before trend chart)
                    if (state.compareData != null) {
                        item(key = "compare_card") {
                            CompareCard(
                                data = state.compareData,
                                onShare = {
                                    navigator.push(
                                        ShareCardScreen(
                                            beforeId = state.compareData.before.id,
                                            afterId = state.compareData.after.id,
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }

                    // 2. Trend chart card (with metric chips inside)
                    if (state.chartPoints.size >= 2) {
                        item(key = "trend_chart") {
                            TrendChartSection(
                                chartPoints = state.chartPoints,
                                selectedMetric = selectedMetric,
                                onMetricSelect = { viewModel.setMetric(it) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }

                    // 3. Attribution entry card
                    if (state.records.size >= 3) {
                        item(key = "attribution_entry") {
                            AttributionEntryCard(
                                onClick = { navigator.push(AttributionReportScreen()) },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }

                    // 4. "近期记录" section header
                    item(key = "records_header") {
                        Text(
                            text = "近期记录",
                            fontSize = 12.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 0.5.sp,
                        )
                    }

                    // 5. Record list
                    itemsIndexed(state.records, key = { _, it -> it.id }) { index, record ->
                        val previousScore = state.records.getOrNull(index + 1)?.overallScore
                        val scoreDiff = if (record.overallScore != null && previousScore != null) {
                            record.overallScore - previousScore
                        } else null
                        TimelineRecordItem(
                            record = record,
                            scoreDiff = scoreDiff,
                            onClick = { navigator.push(RecordDetailScreen(record.id)) },
                            modifier = Modifier.animateListItem(index),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrendChartSection(
    chartPoints: List<ChartRecord>,
    selectedMetric: ChartMetric,
    onMetricSelect: (ChartMetric) -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.md),
        ) {
            SectionHeader(title = "趋势分析")

            // Metric filter chips inside the trend card
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.iconGap),
                modifier = Modifier.padding(
                    bottom = MaterialTheme.spacing.listGap,
                ),
            ) {
                items(ChartMetric.entries) { metric ->
                    FilterChip(
                        selected = selectedMetric == metric,
                        onClick = { onMetricSelect(metric) },
                        label = { Text(metric.label) },
                    )
                }
            }

            TrendChart(
                points = chartPoints,
                metric = selectedMetric,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimelineRecordItem(
    record: SkinRecord,
    scoreDiff: Int? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md, vertical = spacing.listGap),
            horizontalArrangement = Arrangement.spacedBy(spacing.listGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Thumbnail 56dp with radius-md (12dp)
            val imagePath = record.localImagePath
            if (imagePath != null) {
                AsyncImage(
                    model = pathToImageModel(imagePath),
                    contentDescription = "皮肤照片",
                    modifier = Modifier
                        .size(56.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                // Date + change badge
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = formatShortDate(record.recordedAt),
                        fontSize = 14.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        letterSpacing = 0.05.sp,
                    )
                    if (scoreDiff != null) {
                        val functional = MaterialTheme.extendedColors.functional
                        val (text, bgColor, fgColor) = when {
                            scoreDiff > 0 -> Triple(
                                "↑+$scoreDiff",
                                Color(0xFFECFDF5),
                                functional.success,
                            )
                            scoreDiff < 0 -> Triple(
                                "↓$scoreDiff",
                                Color(0xFFFEF2F2),
                                MaterialTheme.colorScheme.error,
                            )
                            else -> Triple(
                                "→ 0",
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Text(
                            text = text,
                            fontSize = 10.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = fgColor,
                            letterSpacing = 0.3.sp,
                            modifier = Modifier
                                .background(
                                    color = bgColor,
                                    shape = FullRoundedShape,
                                )
                                .padding(
                                    horizontal = 8.dp,
                                    vertical = 2.dp,
                                ),
                        )
                    }
                }

                // Summary text (b3: 13px/400/1.40/0.05px)
                Text(
                    text = record.skinType.displayName,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                    lineHeight = 18.sp,
                    letterSpacing = 0.05.sp,
                )
            }

            // Mini ScoreRing 44dp with subtle glow
            if (record.overallScore != null) {
                Box(
                    modifier = Modifier
                        .shadow(
                            elevation = 6.dp,
                            shape = androidx.compose.foundation.shape.CircleShape,
                            ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        ),
                ) {
                    ScoreRing(
                        score = record.overallScore,
                        size = 44.dp,
                        strokeWidth = 3.5.dp,
                        label = "",
                    )
                }
            } else {
                Text(
                    text = "待分析",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun TimelineEmptyState(
    onStartCamera: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    Box(modifier = modifier) {
        // Decorative floating dots
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 32.dp, top = 60.dp)
                .size(8.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(Primary300.copy(alpha = 0.3f)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 48.dp, top = 80.dp)
                .size(6.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(Rose300.copy(alpha = 0.25f)),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 56.dp, bottom = 120.dp)
                .size(5.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(Lavender300.copy(alpha = 0.2f)),
        )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Illustration circle
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(
                    brush = androidx.compose.ui.graphics.Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                            MaterialTheme.colorScheme.primaryContainer,
                        ),
                    ),
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "\uD83D\uDCF7",
                style = MaterialTheme.typography.displayLarge,
            )
        }

        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier.size(spacing.lg),
        )

        // h3: 19px/700
        Text(
            text = "还没有记录哦",
            fontSize = 19.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            letterSpacing = (-0.2).sp,
        )

        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier.size(spacing.sm), // 8dp matches space-8
        )

        Text(
            text = "拍第一张自拍，开始追踪你的皮肤变化吧~",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            letterSpacing = 0.05.sp,
        )

        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier.size(spacing.lg), // 24dp matches space-24
        )

        // Step guide
        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.lg),
        ) {
            TimelineStepItem(
                number = 1,
                text = "拍照",
                isPrimary = true,
            )
            TimelineStepItem(
                number = 2,
                text = "分析",
                isPrimary = false,
            )
            TimelineStepItem(
                number = 3,
                text = "追踪",
                isPrimary = false,
            )
        }

        androidx.compose.foundation.layout.Spacer(
            modifier = Modifier.size(spacing.xl), // 32dp matches space-32
        )

        // CTA Button
        androidx.compose.material3.Button(
            onClick = onStartCamera,
            modifier = Modifier.fillMaxWidth(),
            shape = FullRoundedShape,
        ) {
            Text(
                text = "开始拍照",
                fontSize = 16.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                letterSpacing = (-0.1).sp,
            )
        }
    }
    } // Box
}

@Composable
private fun TimelineStepItem(
    number: Int,
    text: String,
    isPrimary: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.iconGap),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = if (isPrimary) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    shape = androidx.compose.foundation.shape.CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "$number",
                fontSize = 12.sp,
                color = if (isPrimary) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                letterSpacing = 0.2.sp,
            )
        }
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 13.sp,
            letterSpacing = 0.3.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttributionEntryCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            ) {
                Text(
                    text = "归因分析报告",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "查看产品对皮肤的影响",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = "→",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
