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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skintrack.app.ui.component.ScoreRing
import com.skintrack.app.ui.component.SectionHeader
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

// -- Sample data --

private data class SampleRecord(
    val date: String,       // short date: "3月12日 周三"
    val score: Int,
    val summary: String,
    val change: Int,        // positive = improved, negative = worse, 0 = no change
)

private val sampleRecords = listOf(
    SampleRecord("3月14日 周五", 82, "皮肤状态超棒的，痘痘在消退中~", change = +4),
    SampleRecord("3月13日 周四", 78, "肤色越来越均匀了，继续加油~", change = +2),
    SampleRecord("3月12日 周三", 74, "毛孔在变细腻，效果看得见!", change = +3),
    SampleRecord("3月11日 周二", 70, "状态稳定向好，保持得不错哦", change = 0),
    SampleRecord("3月10日 周一", 70, "昨晚熬夜啦，记得早点休息哦~", change = -2),
)

private val chartScores = listOf(70, 70, 74, 78, 82)

private val metricChips = listOf("总评分", "痘痘", "毛孔", "均匀度", "水润")

private val filterChips = listOf("全部", "本周", "本月", "3个月")

// -- Content preview (mirrors TimelineScreen structure) --

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimelineContentPreview() {
    val spacing = MaterialTheme.spacing

    Column(modifier = Modifier.fillMaxSize()) {
        // Title
        Text(
            text = "肌肤记录",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(
                horizontal = spacing.md,
                vertical = spacing.sm,
            ),
        )

        // Time filter chips (outside LazyColumn, matching real screen)
        LazyRow(
            contentPadding = PaddingValues(horizontal = spacing.md),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            filterChips.forEachIndexed { index, label ->
                item {
                    FilterChip(
                        selected = index == 0,
                        onClick = {},
                        label = { Text(label) },
                    )
                }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(
                horizontal = spacing.md,
                vertical = spacing.sm,
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            // 1. Compare card (first, matching real screen order)
            item(key = "compare_card") {
                CompareCardPreview(modifier = Modifier.fillMaxWidth())
            }

            // 2. Trend chart card (with metric chips inside)
            item(key = "trend_chart") {
                TrendChartSectionPreview(modifier = Modifier.fillMaxWidth())
            }

            // 3. Attribution entry card
            item(key = "attribution_entry") {
                Card(modifier = Modifier.fillMaxWidth()) {
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
                            text = "\u2192",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // 4. "所有记录" section header
            item(key = "records_header") {
                SectionHeader(
                    title = "所有记录",
                    trailing = {
                        Text(
                            text = "${sampleRecords.size} 条",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                )
            }

            // 5. Record items list
            itemsIndexed(sampleRecords, key = { i, _ -> "record_$i" }) { index, record ->
                TimelineRecordItemPreview(
                    record = record,
                    modifier = Modifier.animateListItem(index),
                )
            }
        }
    }
}

// -- Trend chart section (card with metric chips inside, matching TrendChartSection) --

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrendChartSectionPreview(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing

    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.md),
        ) {
            SectionHeader(
                title = "趋势变化",
                trailing = {
                    Text(
                        text = "详情",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
            )

            // Metric filter chips inside the trend card
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                modifier = Modifier.padding(
                    top = spacing.sm,
                    bottom = spacing.sm,
                ),
            ) {
                metricChips.forEachIndexed { index, label ->
                    item {
                        FilterChip(
                            selected = index == 0,
                            onClick = {},
                            label = { Text(label) },
                        )
                    }
                }
            }

            // Simplified trend chart canvas
            TrendChartCanvasPreview(
                scores = chartScores,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

// -- Simplified TrendChart Canvas --

@Composable
private fun TrendChartCanvasPreview(
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

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight),
        ) {
            val paddingLeft = 40f
            val paddingBottom = 24f
            val chartW = size.width - paddingLeft
            val chartH = size.height - paddingBottom

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

// -- CompareCard preview (matches design: rich photo comparison) --

@Composable
private fun CompareCardPreview(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing
    val functional = MaterialTheme.extendedColors.functional

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFF0FAF6),
                            Color(0xFFFFF5F6),
                            Color(0xFFF5F0FF),
                        ),
                    ),
                )
                .padding(spacing.md),
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
                    fontWeight = FontWeight.Bold,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "分享",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))

            // Photo row with VS badge
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    // Before photo
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(160.dp)
                            .clip(MaterialTheme.shapes.large)
                            .background(Color(0xFFD4B8A8)),
                        contentAlignment = Alignment.BottomStart,
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Before",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f),
                            )
                            Text(
                                text = "72",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                            )
                        }
                        Text(
                            text = "3月1日",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp),
                        )
                    }
                    // After photo
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(160.dp)
                            .clip(MaterialTheme.shapes.large)
                            .background(Color(0xFFE8CFC0)),
                        contentAlignment = Alignment.BottomStart,
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "After",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f),
                            )
                            Text(
                                text = "82",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                            )
                        }
                        Text(
                            text = "3月14日",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp),
                        )
                    }
                }
                // VS badge
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .align(Alignment.Center)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "VS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Delta row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "\u2191 +10 分",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = functional.success,
                )
                Text(
                    text = " \u00B7 2周内显著改善",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

// -- Record item (matches real TimelineRecordItem structure) --

@Composable
private fun TimelineRecordItemPreview(
    record: SampleRecord,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    val functional = MaterialTheme.extendedColors.functional

    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Thumbnail 56dp (matching real TimelineRecordItem)
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            )

            // Date + change badge row, then skin type below
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                // Date + change badge on same row
                Row(
                    horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = record.date,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.2).sp,
                    )
                    val (badgeText, bgColor, fgColor) = when {
                        record.change > 0 -> Triple(
                            "\u2191+${record.change}",
                            Color(0xFFECFDF5),
                            functional.success,
                        )
                        record.change < 0 -> Triple(
                            "\u2193${record.change}",
                            Color(0xFFFEF2F2),
                            MaterialTheme.colorScheme.error,
                        )
                        else -> Triple(
                            "\u2192 0",
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = fgColor,
                        modifier = Modifier
                            .background(
                                color = bgColor,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                            )
                            .padding(
                                horizontal = 6.dp,
                                vertical = 1.dp,
                            ),
                    )
                }

                // Summary text
                Text(
                    text = record.summary,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp,
                )
            }

            // Mini ScoreRing 44dp (matching real TimelineRecordItem)
            ScoreRing(
                score = record.score,
                size = 44.dp,
                strokeWidth = 3.5.dp,
                label = "",
            )
        }
    }
}

// -- Empty state preview --

@Composable
private fun TimelineEmptyPreview() {
    val spacing = MaterialTheme.spacing

    Column(modifier = Modifier.fillMaxSize()) {
        // Title (same as content state)
        Text(
            text = "肌肤记录",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(
                horizontal = spacing.md,
                vertical = spacing.sm,
            ),
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Illustration circle
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                    MaterialTheme.colorScheme.primaryContainer,
                                ),
                            ),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "\uD83D\uDCF7", style = MaterialTheme.typography.displayLarge)
                }

                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(spacing.lg))

                Text(
                    text = "你的第一次记录将从这里开始",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )

                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(spacing.sm))

                Text(
                    text = "拍下第一张素颜照，AI 帮你分析肌肤状态。\n坚持记录，你会看到皮肤一天天在变好~",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 21.sp,
                )

                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(28.dp))

                // Step guide
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TimelineStepItemPreview(number = 1, text = "自拍一张\n素颜照", isPrimary = true, modifier = Modifier.weight(1f))
                    TimelineStepItemPreview(number = 2, text = "AI 自动\n分析评分", isPrimary = false, modifier = Modifier.weight(1f))
                    TimelineStepItemPreview(number = 3, text = "查看趋势\n变化", isPrimary = false, modifier = Modifier.weight(1f))
                }

                androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(28.dp))

                // CTA Button
                androidx.compose.material3.Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth(),
                    shape = com.skintrack.app.ui.theme.FullRoundedShape,
                ) {
                    Text(
                        text = "开始第一次记录",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineStepItemPreview(
    number: Int,
    text: String,
    isPrimary: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(
                    if (isPrimary) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "$number",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isPrimary) MaterialTheme.colorScheme.onPrimary
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 16.sp,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
