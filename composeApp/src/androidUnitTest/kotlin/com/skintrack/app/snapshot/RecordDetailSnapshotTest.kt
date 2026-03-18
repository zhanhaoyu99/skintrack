package com.skintrack.app.snapshot

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skintrack.app.ui.component.RadarChart
import com.skintrack.app.ui.component.RadarMetric
import com.skintrack.app.ui.component.ScoreRing
import com.skintrack.app.ui.component.SectionCard
import com.skintrack.app.ui.component.SectionHeader
import com.skintrack.app.ui.theme.FullRoundedShape
import com.skintrack.app.ui.theme.Motion
import com.skintrack.app.ui.theme.extendedColors
import com.skintrack.app.ui.theme.spacing
import org.junit.Test

class RecordDetailSnapshotTest : SnapshotTestBase() {

    @Test
    fun recordDetail_light() = captureLight {
        RecordDetailPreview()
    }

    @Test
    fun recordDetail_dark() = captureDark {
        RecordDetailPreview()
    }

    @Test
    fun radarChart_light() = captureLight {
        RadarChartPreview()
    }

    @Test
    fun radarChart_dark() = captureDark {
        RadarChartPreview()
    }
}

@Composable
private fun RecordDetailPreview() {
    val spacing = MaterialTheme.spacing
    val skinMetric = MaterialTheme.extendedColors.skinMetric

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        // Floating score card - matching FloatingScoreCard in RecordDetailScreen
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ScoreRing(
                        score = 82,
                        size = 74.dp,
                        strokeWidth = 5.dp,
                    )
                    Column(
                        verticalArrangement = Arrangement.spacedBy(spacing.xs),
                    ) {
                        Text(
                            text = "你的肌肤状态很棒哦",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.3).sp,
                        )
                        Text(
                            text = "整体评分超过 78% 的用户",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        // Score change pill
                        val functional = MaterialTheme.extendedColors.functional
                        Text(
                            text = "\u2191 +4 较上次",
                            style = MaterialTheme.typography.labelMedium,
                            color = functional.success,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(
                                    color = functional.success.copy(alpha = 0.1f),
                                    shape = FullRoundedShape,
                                )
                                .padding(horizontal = spacing.sm, vertical = spacing.xs),
                        )
                    }
                }
            }
        }

        // Radar chart
        item {
            SectionCard {
                SectionHeader("多维评分")
                RadarChart(
                    metrics = sampleMetrics(skinMetric),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        // Metric bars - matching MetricBarRow in RecordDetailScreen (gradient bars + change values)
        item {
            data class MetricData(val label: String, val score: Int, val gradientStart: Color, val gradientEnd: Color, val change: Int)
            val metrics = listOf(
                MetricData("痘痘", 85, Color(0xFFFF8A8A), skinMetric.acne, 5),
                MetricData("毛孔", 78, Color(0xFF7EDCD4), skinMetric.pore, 3),
                MetricData("均匀度", 82, Color(0xFFFFEE8D), skinMetric.evenness, 6),
                MetricData("泛红", 75, Color(0xFFF5A1A1), skinMetric.redness, 1),
                MetricData("水润", 80, Color(0xFF75C7E1), skinMetric.hydration, 4),
            )
            SectionCard {
                SectionHeader("各项指标")
                metrics.forEachIndexed { index, m ->
                    if (index > 0) {
                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant,
                            thickness = 0.5.dp,
                        )
                    }
                    MetricBarRowPreview(
                        label = m.label,
                        score = m.score,
                        gradientStart = m.gradientStart,
                        gradientEnd = m.gradientEnd,
                        change = m.change,
                    )
                }
            }
        }

        // AI summary - matching AiSummaryCard in RecordDetailScreen
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
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

                    Text(
                        text = "整体状态很不错哦~ 痘痘较上次减少了约 30%，主要集中在下巴区域。肤色均匀度提升明显，说明当前的护肤方案很适合你，继续坚持!",
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 24.sp,
                    )

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
                        Text(
                            text = "护肤建议: T 区毛孔稍需关注，可以试试含水杨酸的收敛精华，每周 2-3 次局部使用。",
                            style = MaterialTheme.typography.bodySmall,
                            lineHeight = 20.sp,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Snapshot-friendly MetricBarRow matching RecordDetailScreen.MetricBarRow design.
 */
@Composable
private fun MetricBarRowPreview(
    label: String,
    score: Int,
    gradientStart: Color,
    gradientEnd: Color,
    change: Int? = null,
    maxScore: Int = 100,
) {
    val spacing = MaterialTheme.spacing
    val targetFraction = (score.toFloat() / maxScore).coerceIn(0f, 1f)
    val fraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = tween(
            durationMillis = Motion.LONG,
            easing = Motion.EmphasizedDecelerate,
        ),
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Label (52dp width, 13sp per design)
        Text(
            text = label,
            fontSize = 13.sp,
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

        // Score value (15sp/800 per design)
        Text(
            text = "$score",
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
        )

        // Change value (36dp width per design)
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
                modifier = Modifier.width(36.dp),
                textAlign = TextAlign.End,
            )
        }
    }
}

@Composable
private fun RadarChartPreview() {
    val skinMetric = MaterialTheme.extendedColors.skinMetric
    SectionCard {
        SectionHeader("多维评分")
        RadarChart(
            metrics = sampleMetrics(skinMetric),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// Order matches design spec: 综合→毛孔→泛红→水润→均匀→痘痘 (clockwise)
@Composable
private fun sampleMetrics(skinMetric: com.skintrack.app.ui.theme.SkinMetricColors) = listOf(
    RadarMetric("整体", 82f, color = MaterialTheme.colorScheme.primary),
    RadarMetric("毛孔", 78f, color = skinMetric.pore),
    RadarMetric("泛红", 75f, color = skinMetric.redness),
    RadarMetric("水润", 80f, color = skinMetric.hydration),
    RadarMetric("均匀度", 82f, color = skinMetric.evenness),
    RadarMetric("痘痘", 85f, color = skinMetric.acne),
)
