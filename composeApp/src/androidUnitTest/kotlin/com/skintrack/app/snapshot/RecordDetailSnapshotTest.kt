package com.skintrack.app.snapshot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.skintrack.app.ui.component.RadarChart
import com.skintrack.app.ui.component.RadarMetric
import com.skintrack.app.ui.component.ScoreBar
import com.skintrack.app.ui.component.ScoreRing
import com.skintrack.app.ui.component.SectionCard
import com.skintrack.app.ui.component.SectionHeader
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
        // Score overview with ScoreRing
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.md, vertical = spacing.lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    ScoreRing(score = 82)
                    Text(
                        text = "混合型",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        }

        // Radar chart
        item {
            SectionCard {
                SectionHeader("皮肤维度分析")
                RadarChart(
                    metrics = sampleMetrics(skinMetric),
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        // Metric bars
        item {
            SectionCard {
                SectionHeader("指标详情")
                ScoreBar(label = "综合", score = 82, color = MaterialTheme.colorScheme.primary)
                ScoreBar(label = "毛孔", score = 75, color = skinMetric.pore)
                ScoreBar(label = "泛红", score = 68, color = skinMetric.redness)
                ScoreBar(label = "均匀度", score = 88, color = skinMetric.evenness)
                ScoreBar(label = "黑头", score = 72, color = skinMetric.hydration)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(text = "痘痘", style = MaterialTheme.typography.bodyMedium)
                    Text(
                        text = "3 个",
                        style = MaterialTheme.typography.bodyMedium,
                        color = skinMetric.acne,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        // AI summary
        item {
            SectionCard {
                SectionHeader("AI 分析摘要")
                Text(
                    text = "皮肤整体状态良好，毛孔和泛红指标处于中等水平，建议加强保湿和防晒。",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    text = "建议",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = spacing.xs),
                )
                Text(
                    text = "· 加强日常保湿护理",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "· 使用含烟酰胺的产品改善肤色不均",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun RadarChartPreview() {
    val skinMetric = MaterialTheme.extendedColors.skinMetric
    SectionCard {
        SectionHeader("皮肤维度分析")
        RadarChart(
            metrics = sampleMetrics(skinMetric),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun sampleMetrics(skinMetric: com.skintrack.app.ui.theme.SkinMetricColors) = listOf(
    RadarMetric("综合", 82f, color = MaterialTheme.colorScheme.primary),
    RadarMetric("毛孔", 75f, color = skinMetric.pore),
    RadarMetric("泛红", 68f, color = skinMetric.redness),
    RadarMetric("均匀度", 88f, color = skinMetric.evenness),
    RadarMetric("黑头", 72f, color = skinMetric.hydration),
    RadarMetric("痘痘", 85f, color = skinMetric.acne),
)
