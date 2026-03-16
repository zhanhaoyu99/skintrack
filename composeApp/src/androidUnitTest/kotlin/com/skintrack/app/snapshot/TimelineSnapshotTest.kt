package com.skintrack.app.snapshot

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import com.skintrack.app.ui.component.EmptyContent
import com.skintrack.app.ui.component.ScoreRing
import com.skintrack.app.ui.component.TrendIndicator
import com.skintrack.app.ui.component.animateListItem
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.extendedColors
import com.skintrack.app.ui.theme.gradients
import com.skintrack.app.ui.theme.spacing
import org.junit.Test

class TimelineSnapshotTest : SnapshotTestBase() {

    @Test
    fun timeline_content_light() = captureLight {
        TimelineContentPreview()
    }

    @Test
    fun timeline_content_dark() = captureDark {
        TimelineContentPreview()
    }

    @Test
    fun timeline_empty_light() = captureLight {
        TimelineEmptyPreview()
    }

    @Test
    fun timeline_empty_dark() = captureDark {
        TimelineEmptyPreview()
    }
}

// ── Sample data ─────────────────────────────────────────────────────

private data class SampleRecord(
    val date: String,
    val score: Int,
    val skinType: String,
    val change: Int, // positive = improved, negative = worse, 0 = no change
    val hasImage: Boolean = true,
)

private val sampleRecords = listOf(
    SampleRecord("2026年3月12日 09:30", 82, "混合", change = +4),
    SampleRecord("2026年3月11日 08:45", 78, "混合", change = +3),
    SampleRecord("2026年3月10日 09:15", 75, "干性", change = +4),
    SampleRecord("2026年3月9日 10:00", 71, "混合", change = +2),
    SampleRecord("2026年3月8日 08:30", 69, "混合", change = 0),
)

private val chartScores = listOf(69, 71, 75, 78, 82)

private val metricChips = listOf("总评分", "痘痘", "毛孔", "均匀度")

// ── Content preview ──────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimelineContentPreview() {
    val spacing = MaterialTheme.spacing
    val dimens = MaterialTheme.dimens

    Column(modifier = Modifier.fillMaxSize()) {
        // Title (matches real TimelineScreen: Text with headlineSmall)
        Text(
            text = "皮肤记录",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(
                horizontal = spacing.md,
                vertical = spacing.sm,
            ),
        )

        LazyColumn(
            contentPadding = PaddingValues(
                horizontal = spacing.md,
                vertical = spacing.sm,
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            // ── TrendChart (simplified Canvas reproduction) ──────────
            item(key = "trend_chart") {
                TrendChartPreview(
                    scores = chartScores,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = spacing.md),
                )
            }

            // ── Metric filter chips ─────────────────────────────────
            item(key = "metric_chips") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    metricChips.forEachIndexed { index, label ->
                        FilterChip(
                            selected = index == 0,
                            onClick = {},
                            label = { Text(label) },
                        )
                    }
                }
            }

            // ── CompareCard with VS badge ───────────────────────────
            item(key = "compare_card") {
                CompareCardPreview(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = spacing.md),
                )
            }

            // ── Attribution entry card (>= 3 records) ──────────────
            item(key = "attribution_entry") {
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = spacing.md)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.md),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(spacing.xs),
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

            // ── Record items list ────────────────────────────────────
            itemsIndexed(sampleRecords, key = { i, _ -> "record_$i" }) { index, record ->
                TimelineRecordItemPreview(
                    record = record,
                    modifier = Modifier.animateListItem(index),
                )
            }
        }
    }
}

// ── Simplified TrendChart ────────────────────────────────────────────

@Composable
private fun TrendChartPreview(
    scores: List<Int>,
    modifier: Modifier = Modifier,
) {
    val lineColor = MaterialTheme.colorScheme.primary
    val fillColor = lineColor.copy(alpha = 0.1f)
    val chartHeight = MaterialTheme.dimens.chartHeight
    val lineWidth = MaterialTheme.dimens.chartLineWidth
    val dotRadius = MaterialTheme.dimens.chartDotRadius
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val labelStyle = MaterialTheme.typography.labelSmall

    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(MaterialTheme.spacing.md)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight),
            ) {
                val paddingLeft = 40f
                val paddingBottom = 24f
                val chartW = size.width - paddingLeft
                val chartH = size.height - paddingBottom

                // Map scores to canvas coordinates
                val coords = scores.mapIndexed { i, score ->
                    val x = paddingLeft + chartW * i / (scores.size - 1).toFloat()
                    val y = chartH * (1f - score / 100f)
                    Offset(x, y)
                }

                // Fill area
                val fillPath = Path().apply {
                    coords.forEachIndexed { i, offset ->
                        if (i == 0) moveTo(offset.x, offset.y)
                        else lineTo(offset.x, offset.y)
                    }
                    lineTo(coords.last().x, chartH)
                    lineTo(coords.first().x, chartH)
                    close()
                }
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(fillColor, Color.Transparent),
                        startY = 0f,
                        endY = chartH,
                    ),
                )

                // Line
                val linePath = Path().apply {
                    coords.forEachIndexed { i, offset ->
                        if (i == 0) moveTo(offset.x, offset.y)
                        else lineTo(offset.x, offset.y)
                    }
                }
                drawPath(
                    path = linePath,
                    color = lineColor,
                    style = Stroke(width = lineWidth.toPx()),
                )

                // Dots
                coords.forEach { offset ->
                    drawCircle(
                        color = lineColor,
                        radius = dotRadius.toPx(),
                        center = offset,
                    )
                }
            }

            // X-axis date labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "03/08",
                    style = labelStyle,
                    color = labelColor,
                )
                Text(
                    text = "03/12",
                    style = labelStyle,
                    color = labelColor,
                )
            }
        }
    }
}

// ── CompareCard preview ─────────────────────────────────────────────

@Composable
private fun CompareCardPreview(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    val dimens = MaterialTheme.dimens

    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.gradients.warm)
                .padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            // Title row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "前后对比",
                    style = MaterialTheme.typography.titleMedium,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "4天",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    TextButton(onClick = {}) {
                        Text("分享", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            // Photo row: before / VS / after (placeholder boxes)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                // Before photo placeholder
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimens.photoCompareHeight)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "Before",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text = "03/08",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                // After photo placeholder
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimens.photoCompareHeight)
                            .clip(MaterialTheme.shapes.medium)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "After",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text = "03/12",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Score comparison row with TrendIndicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "69",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Text(
                        text = "之前",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }

                TrendIndicator(value = 13)

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "82",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    Text(
                        text = "之后",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

// ── Record item with change badge ───────────────────────────────────

@Composable
private fun TimelineRecordItemPreview(
    record: SampleRecord,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    val dimens = MaterialTheme.dimens
    val functional = MaterialTheme.extendedColors.functional

    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Thumbnail placeholder (colored box, 80dp matching thumbnailSize)
            Box(
                modifier = Modifier
                    .size(dimens.thumbnailSize)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Photo",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Date + skin type + change badge
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                Text(
                    text = record.date,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = record.skinType,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    // Change badge
                    ChangeBadge(change = record.change)
                }
            }

            // ScoreRing on the right (thumbnailSize = 72dp)
            ScoreRing(
                score = record.score,
                size = dimens.thumbnailSize,
                strokeWidth = dimens.chartDotRadius,
                label = "",
            )
        }
    }
}

/**
 * Change badge showing score difference:
 * - Positive: green "↑+N"
 * - Zero: neutral "→0"
 * - Negative: red "↓-N"
 */
@Composable
private fun ChangeBadge(change: Int) {
    val functional = MaterialTheme.extendedColors.functional
    val (text, color) = when {
        change > 0 -> "↑+$change" to functional.success
        change < 0 -> "↓$change" to functional.error
        else -> "→0" to MaterialTheme.colorScheme.onSurfaceVariant
    }
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = color,
    )
}

// ── Empty state preview ──────────────────────────────────────────────

@Composable
private fun TimelineEmptyPreview() {
    Column(modifier = Modifier.fillMaxSize()) {
        // Title (same as content state)
        Text(
            text = "皮肤记录",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.md,
                vertical = MaterialTheme.spacing.sm,
            ),
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            EmptyContent(message = "还没有记录，去拍一张吧")
        }
    }
}
