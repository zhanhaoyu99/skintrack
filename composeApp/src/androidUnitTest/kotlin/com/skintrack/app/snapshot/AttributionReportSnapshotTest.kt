package com.skintrack.app.snapshot

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skintrack.app.ui.component.SectionCard
import com.skintrack.app.ui.component.SectionHeader
import com.skintrack.app.ui.component.animateListItem
import com.skintrack.app.ui.theme.Mint50
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
    MockAttribution("CeraVe 洁面乳", "洁面", 14, 4.2f),
    MockAttribution("薇诺娜 特护精华液", "精华", 12, 3.8f),
    MockAttribution("安耐晒 金瓶防晒", "防晒", 10, 2.1f),
    MockAttribution("珂润 保湿面霜", "面霜", 14, 1.5f),
    MockAttribution("SK-II 神仙水", "化妆水", 8, 0.3f),
)

private val sampleSuggestions = listOf(
    "保持当前的洁面+精华组合，与肌肤改善呈强正相关",
    "建议增加夜间修复类产品，帮助肌肤在夜间更好修复",
    "防晒使用频率可以提高，建议每天出门前使用",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttributionPreview() {
    val spacing = MaterialTheme.spacing
    val functional = MaterialTheme.extendedColors.functional
    val firstScore = 70
    val lastScore = 82
    val trendDelta = 12

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
            // Before/After score comparison
            item(key = "score_compare") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = spacing.lg, end = spacing.lg, top = spacing.sm, bottom = 20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .border(2.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "$firstScore",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Text(
                            text = "14天前",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                    Column(
                        modifier = Modifier.padding(horizontal = spacing.md),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(
                            text = "\u2192",
                            style = MaterialTheme.typography.titleLarge,
                            color = functional.success,
                        )
                        Text(
                            text = "+$trendDelta",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = functional.success,
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .shadow(4.dp, CircleShape)
                                .border(2.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                .background(Mint50, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "$lastScore",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        Text(
                            text = "今天",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 6.dp),
                        )
                    }
                }
            }

            // Overview card
            item(key = "overview") {
                SectionCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        SectionHeader("分析概览")
                        Text(
                            text = "14 天",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.shapes.small,
                                )
                                .padding(horizontal = spacing.sm, vertical = spacing.xs),
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = spacing.sm),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        data class StatItem(val value: String, val label: String, val brush: Brush, val valueColor: Color)
                        listOf(
                            StatItem("+12", "评分变化", Brush.linearGradient(listOf(Color(0xFFECFDF5), Color(0xFFD1FAE5))), functional.success),
                            StatItem("5", "使用产品", Brush.linearGradient(listOf(Mint50, Color(0xFFD1F0E4))), MaterialTheme.colorScheme.primary),
                            StatItem("14", "分析天数", Brush.linearGradient(listOf(Color(0xFFEDE9FE), Color(0xFFDDD6FE))), Color(0xFF7C3AED)),
                        ).forEach { item ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(item.brush, MaterialTheme.shapes.medium)
                                    .padding(horizontal = spacing.sm, vertical = 14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(spacing.xs),
                            ) {
                                Text(
                                    text = item.value,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = item.valueColor,
                                )
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            }

            // AI Insight card
            item(key = "ai_insight") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                listOf(Color(0xFFF0FAF6), Color(0xFFF5F0FF), Color(0xFFFFF8F2)),
                            ),
                            shape = MaterialTheme.shapes.large,
                        )
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                            MaterialTheme.shapes.large,
                        )
                        .padding(spacing.md),
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                        Text(
                            text = "\u2728 AI 洞察",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    RoundedCornerShape(50),
                                )
                                .padding(horizontal = spacing.sm, vertical = spacing.xs),
                        )
                        Text(
                            text = "过去 14 天你的皮肤整体评分从 70 提升到了 82，提升了 17%! CeraVe 洁面+薇诺娜精华的组合对你很有效。建议保持现在的晨间方案，晚上可以加一款修复精华会更好~",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp,
                        )
                    }
                }
            }

            // Ranking header
            item(key = "ranking_header") {
                SectionHeader(
                    title = "产品影响排行",
                    modifier = Modifier.padding(top = spacing.sm),
                )
            }

            // Ranking items
            itemsIndexed(sampleAttributions) { index, attribution ->
                val rank = index + 1
                val impactColor = if (attribution.impact >= 0) functional.success else functional.error
                val prefix = if (attribution.impact >= 0) "+" else ""
                val badgeBackground = when (rank) {
                    1 -> Brush.linearGradient(listOf(Color(0xFFFCD34D), Color(0xFFF59E0B)))
                    2 -> Brush.linearGradient(listOf(Color(0xFFE5E7EB), Color(0xFFE5E7EB)))
                    3 -> Brush.linearGradient(listOf(Color(0xFFFED7AA), Color(0xFFFED7AA)))
                    else -> Brush.linearGradient(
                        listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    )
                }
                val badgeTextColor = when (rank) {
                    1 -> Color.White
                    2 -> Color(0xFF6B7280)
                    3 -> Color(0xFFC2410C)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
                val iconInfo = when {
                    attribution.category.contains("洁面") -> Triple(Brush.linearGradient(listOf(Color(0xFFDBEAFE), Color(0xFFBFDBFE))), Color(0xFF2563EB), "\uD83E\uDDF4")
                    attribution.category.contains("精华") -> Triple(Brush.linearGradient(listOf(Color(0xFFF3E5F5), Color(0xFFE1BEE7))), Color(0xFF7B1FA2), "\uD83E\uDDEA")
                    attribution.category.contains("防晒") -> Triple(Brush.linearGradient(listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2))), Color(0xFFE65100), "\u2600\uFE0F")
                    attribution.category.contains("面霜") -> Triple(Brush.linearGradient(listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9))), Color(0xFF2E7D32), "\uD83C\uDF3F")
                    attribution.category.contains("化妆水") -> Triple(Brush.linearGradient(listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2))), Color(0xFF00838F), "\uD83D\uDCA7")
                    else -> Triple(Brush.linearGradient(listOf(Color(0xFFF5F5F5), Color(0xFFEEEEEE))), Color(0xFF616161), "\uD83E\uDDF4")
                }
                val cardColor = if (rank == 1) Color.Transparent else MaterialTheme.colorScheme.surface
                val rank1Gradient = if (rank == 1) Brush.linearGradient(listOf(Color(0xFFFFFBEB), Color(0xFFFEF3C7))) else null

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateListItem(index)
                        .then(
                            if (rank == 1) {
                                Modifier
                                    .shadow(2.dp, MaterialTheme.shapes.medium)
                                    .border(1.dp, Color(0xFFD97706).copy(alpha = 0.12f), MaterialTheme.shapes.medium)
                            } else Modifier,
                        ),
                    colors = CardDefaults.cardColors(containerColor = cardColor),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (rank1Gradient != null) {
                                    Modifier.background(brush = rank1Gradient)
                                } else Modifier,
                            )
                            .padding(horizontal = spacing.md, vertical = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(badgeBackground, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "$rank",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = badgeTextColor,
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(brush = iconInfo.first, shape = RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(text = iconInfo.third, fontSize = 18.sp, textAlign = TextAlign.Center)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = attribution.name,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = "${attribution.category} \u00B7 使用 ${attribution.daysUsed} 天",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${prefix}${attribution.impact}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = impactColor,
                            )
                            Text(
                                text = "影响分",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            // Suggestions
            item(key = "suggestions_header") {
                SectionHeader(
                    title = "改善建议",
                    modifier = Modifier.padding(top = spacing.sm),
                )
            }
            item(key = "suggestions") {
                SectionCard {
                    sampleSuggestions.forEachIndexed { index, text ->
                        if (index > 0) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                thickness = 0.5.dp,
                            )
                        }
                        val iconBgBrush = when (index + 1) {
                            1 -> Brush.linearGradient(listOf(Color(0xFFECFDF5), Color(0xFFD1FAE5)))
                            2 -> Brush.linearGradient(listOf(Color(0xFFEDE9FE), Color(0xFFDDD6FE)))
                            else -> Brush.linearGradient(listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2)))
                        }
                        val iconEmoji = when (index + 1) {
                            1 -> "\u2713"
                            2 -> "\u23F0"
                            else -> "\u2600"
                        }
                        val iconTextColor = when (index + 1) {
                            1 -> Color(0xFF10B981)
                            2 -> Color(0xFF7C3AED)
                            else -> Color(0xFFE65100)
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = spacing.sm),
                            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                            verticalAlignment = Alignment.Top,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(iconBgBrush, CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = iconEmoji,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = iconTextColor,
                                )
                            }
                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f),
                                lineHeight = 20.sp,
                            )
                        }
                    }
                }
            }
        }
    }
}
