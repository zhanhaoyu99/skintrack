package com.skintrack.app.snapshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.skintrack.app.ui.component.SectionCard
import com.skintrack.app.ui.component.SectionHeader
import com.skintrack.app.ui.component.TrendIndicator
import com.skintrack.app.ui.component.animateListItem
import com.skintrack.app.ui.theme.extendedColors
import com.skintrack.app.ui.theme.spacing
import org.junit.Test

class AttributionReportSnapshotTest : SnapshotTestBase() {

    @Test
    fun attribution_light() = captureLight {
        AttributionPreview()
    }

    @Test
    fun attribution_dark() = captureDark {
        AttributionPreview()
    }
}

private data class MockAttribution(
    val name: String,
    val category: String,
    val daysUsed: Int,
    val impact: Float,
)

private val sampleAttributions = listOf(
    MockAttribution("烟酰胺精华", "精华", 12, 3.5f),
    MockAttribution("温和洁面乳", "洁面", 18, 1.2f),
    MockAttribution("美白面膜", "面膜", 4, -0.8f),
    MockAttribution("酒精化妆水", "化妆水", 6, -2.1f),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttributionPreview() {
    val spacing = MaterialTheme.spacing
    val functional = MaterialTheme.extendedColors.functional
    val trendDelta = 5

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("归因分析报告") })

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                horizontal = spacing.md,
                vertical = spacing.sm,
            ),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            item(key = "summary") {
                SectionCard {
                    SectionHeader("综合总结")
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "分析记录数",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "8 条",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "分析周期",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = "2026.02.15 — 2026.03.10",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            item(key = "trend") {
                SectionCard {
                    SectionHeader("综合评分趋势")
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        TrendIndicator(
                            value = trendDelta,
                            style = MaterialTheme.typography.headlineMedium,
                        )
                        Column {
                            Text(
                                text = "上升",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "变化 +$trendDelta 分",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            item(key = "ranking_header") {
                Text(
                    text = "产品影响排行",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = spacing.sm),
                )
            }

            itemsIndexed(sampleAttributions) { index, attribution ->
                val impactColor = if (attribution.impact >= 0) functional.success else functional.error
                val impactLabel = if (attribution.impact >= 0) "有益" else "有害"
                val prefix = if (attribution.impact >= 0) "+" else ""

                Card(modifier = Modifier.fillMaxWidth().animateListItem(index)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.md),
                        horizontalArrangement = Arrangement.spacedBy(spacing.md),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = attribution.name,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                            ) {
                                Text(
                                    text = attribution.category,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = "使用 ${attribution.daysUsed} 天",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${prefix}${attribution.impact}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = impactColor,
                            )
                            Text(
                                text = impactLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = impactColor,
                                modifier = Modifier
                                    .background(
                                        color = impactColor.copy(alpha = 0.12f),
                                        shape = MaterialTheme.shapes.extraSmall,
                                    )
                                    .padding(
                                        horizontal = spacing.sm,
                                        vertical = spacing.xs,
                                    ),
                            )
                        }
                    }
                }
            }
        }
    }
}
