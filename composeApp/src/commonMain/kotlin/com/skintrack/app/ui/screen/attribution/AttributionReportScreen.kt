package com.skintrack.app.ui.screen.attribution

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.skintrack.app.domain.model.ProductAttribution
import com.skintrack.app.domain.model.displayName
import com.skintrack.app.domain.model.FeatureGate
import com.skintrack.app.domain.model.lockedMessage
import com.skintrack.app.ui.component.LoadingContent
import com.skintrack.app.ui.component.LockedFeatureCard
import com.skintrack.app.ui.component.SectionCard
import com.skintrack.app.ui.component.SectionHeader
import com.skintrack.app.ui.component.TrendIndicator
import com.skintrack.app.ui.component.animateListItem
import com.skintrack.app.ui.screen.paywall.PaywallScreen
import com.skintrack.app.ui.theme.Lavender300
import com.skintrack.app.ui.theme.Mint50
import com.skintrack.app.ui.theme.Rose400
import com.skintrack.app.ui.theme.extendedColors
import com.skintrack.app.ui.theme.spacing
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.roundToInt

/**
 * Voyager Screen wrapper — used when pushed onto navigation stack (shows back button).
 */
class AttributionReportScreen : Screen {

    @Composable
    override fun Content() {
        AttributionReportContent(showBackButton = true)
    }
}

/**
 * Tab-friendly composable — called from HomeScreen tab with showBackButton=false.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttributionReportContent(showBackButton: Boolean = false) {
    val navigator = LocalNavigator.currentOrThrow
    val viewModel: AttributionReportViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("归因分析") },
                navigationIcon = {
                    if (showBackButton) {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                        }
                    }
                },
            )
        },
    ) { padding ->
        when (val state = uiState) {
            is AttributionUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    LoadingContent()
                }
            }
            is AttributionUiState.InsufficientData -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                    ) {
                        Text(
                            text = "数据不足",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "至少需要 3 条皮肤记录才能生成归因报告，请多拍几次",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            is AttributionUiState.Content -> {
                ReportContent(state, Modifier.padding(padding))
            }
        }
    }
}

@Composable
private fun ReportContent(
    state: AttributionUiState.Content,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = spacing.md,
            vertical = spacing.sm,
        ),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        // Overview card (replaces old SummaryCard)
        item(key = "overview") {
            OverviewCard(state)
        }

        // Trend card
        item(key = "trend") {
            TrendCard(state)
        }

        if (!state.isPremium) {
            item(key = "locked") {
                val navigator = LocalNavigator.currentOrThrow
                LockedFeatureCard(
                    message = FeatureGate.ATTRIBUTION_REPORT.lockedMessage,
                    onUpgrade = { navigator.push(PaywallScreen()) },
                    tags = listOf("AI洞察", "排行榜", "改善建议"),
                )
            }
        } else if (state.attributions.isNotEmpty()) {
            item(key = "ranking_header") {
                SectionHeader(
                    title = "产品影响排行",
                    modifier = Modifier.padding(top = spacing.sm),
                )
            }
            itemsIndexed(state.attributions, key = { _, it -> it.product.id }) { index, attribution ->
                ProductAttributionItem(
                    attribution = attribution,
                    rank = index + 1,
                    modifier = Modifier.animateListItem(index),
                )
            }

            // Improvement suggestions section
            if (state.suggestions.isNotEmpty()) {
                item(key = "suggestions_header") {
                    SectionHeader(
                        title = "改善建议",
                        modifier = Modifier.padding(top = spacing.sm),
                    )
                }
                item(key = "suggestions") {
                    SectionCard {
                        state.suggestions.forEachIndexed { index, suggestion ->
                            SuggestionItem(number = index + 1, text = suggestion)
                        }
                    }
                }
            }
        } else {
            item(key = "no_product_data") {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "暂无产品使用记录，请在拍照后记录当日使用的护肤产品",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(spacing.md),
                    )
                }
            }
        }
    }
}

@Composable
private fun OverviewCard(state: AttributionUiState.Content) {
    val spacing = MaterialTheme.spacing
    val functional = MaterialTheme.extendedColors.functional

    SectionCard {
        // Title row with badge pill
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SectionHeader("分析概览")
            Text(
                text = "${state.analysisDays} 天",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small,
                    )
                    .padding(horizontal = spacing.sm, vertical = spacing.xs),
            )
        }

        // Mini trend line illustration
        val primaryColor = MaterialTheme.colorScheme.primary
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(vertical = spacing.sm),
        ) {
            val width = size.width
            val height = size.height
            // Simple upward trend line
            val points = listOf(0.6f, 0.5f, 0.55f, 0.4f, 0.45f, 0.3f, 0.25f)
            val path = Path()
            points.forEachIndexed { index, y ->
                val x = width * index / (points.size - 1)
                val yPos = height * y
                if (index == 0) path.moveTo(x, yPos) else path.lineTo(x, yPos)
            }
            drawPath(
                path = path,
                color = primaryColor,
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
            // Dots
            points.forEachIndexed { index, y ->
                val x = width * index / (points.size - 1)
                val yPos = height * y
                drawCircle(color = primaryColor, radius = 3.dp.toPx(), center = Offset(x, yPos))
            }
        }

        // 3 stat sub-cards in a Row (matching design: green, mint, purple)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            // Score Change - green gradient (#ECFDF5 → #D1FAE5)
            val deltaPrefix = if (state.trendDelta >= 0) "+" else ""
            StatSubCard(
                label = "评分变化",
                value = "${deltaPrefix}${state.trendDelta}",
                backgroundColor = Color(0xFFECFDF5),
                valueColor = functional.success,
                modifier = Modifier.weight(1f),
            )
            // Products Used - mint gradient
            StatSubCard(
                label = "使用产品",
                value = "${state.productsUsed}",
                backgroundColor = Mint50,
                valueColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
            )
            // Analysis Days - purple gradient (#EDE9FE)
            StatSubCard(
                label = "分析天数",
                value = "${state.analysisDays}",
                backgroundColor = Color(0xFFEDE9FE),
                valueColor = Color(0xFF7C3AED),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatSubCard(
    label: String,
    value: String,
    backgroundColor: Color,
    valueColor: Color,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = MaterialTheme.shapes.medium,
            )
            .padding(horizontal = spacing.sm, vertical = spacing.md),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacing.xs),
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = valueColor,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun TrendCard(state: AttributionUiState.Content) {
    SectionCard {
        SectionHeader("综合评分趋势")
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        ) {
            TrendIndicator(
                value = state.trendDelta,
                style = MaterialTheme.typography.headlineMedium,
            )
            Column {
                Text(
                    text = state.overallTrend,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                val deltaPrefix = if (state.trendDelta >= 0) "+" else ""
                Text(
                    text = "变化 ${deltaPrefix}${state.trendDelta} 分",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun ProductAttributionItem(
    attribution: ProductAttribution,
    rank: Int,
    modifier: Modifier = Modifier,
) {
    val functional = MaterialTheme.extendedColors.functional
    val impactColor = if (attribution.impact >= 0) functional.success else functional.error
    val impactLabel = if (attribution.impact >= 0) "有益" else "有害"
    val spacing = MaterialTheme.spacing

    // Rank badge colors matching design: gold, lavender, rose
    val badgeBackground = when (rank) {
        1 -> Brush.linearGradient(listOf(Color(0xFFFCD34D), Color(0xFFF59E0B)))
        2 -> Brush.linearGradient(listOf(Lavender300, Lavender300))
        3 -> Brush.linearGradient(listOf(Rose400, Rose400))
        else -> Brush.linearGradient(
            listOf(
                MaterialTheme.colorScheme.surfaceVariant,
                MaterialTheme.colorScheme.surfaceVariant,
            ),
        )
    }
    val badgeTextColor = when (rank) {
        1, 2, 3 -> Color.White
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    // Rank 1 gets highlighted card background (warm gold)
    val cardContainerColor = if (rank == 1) {
        Color(0xFFFFFBEB)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardContainerColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.md),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Rank badge circle (30dp per design)
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(
                        brush = badgeBackground,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "$rank",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = badgeTextColor,
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attribution.product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${attribution.product.category.displayName} \u00B7 使用 ${attribution.daysUsed} 天",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                val prefix = if (attribution.impact >= 0) "+" else ""
                Text(
                    text = "${prefix}${formatImpact(attribution.impact)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = impactColor,
                )
                Text(
                    text = "影响分",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SuggestionItem(
    number: Int,
    text: String,
) {
    val spacing = MaterialTheme.spacing

    // Color scheme per suggestion: green, purple, orange
    val iconBgColor = when (number) {
        1 -> Color(0xFFECFDF5)
        2 -> Color(0xFFEDE9FE)
        3 -> Color(0xFFFFF3E0)
        else -> MaterialTheme.colorScheme.primaryContainer
    }
    val iconEmoji = when (number) {
        1 -> "\u2713"  // checkmark
        2 -> "\u23F0"  // clock
        3 -> "\u2600"  // sun
        else -> "$number"
    }
    val iconTextColor = when (number) {
        1 -> Color(0xFF10B981)
        2 -> Color(0xFF7C3AED)
        3 -> Color(0xFFE65100)
        else -> MaterialTheme.colorScheme.onPrimaryContainer
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        verticalAlignment = Alignment.Top,
    ) {
        // Colored circle icon (32dp per design)
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = iconBgColor,
                    shape = CircleShape,
                ),
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
        )
    }
}

private fun formatImpact(value: Float): String {
    val rounded = (value * 10).roundToInt() / 10f
    return if (rounded == rounded.toInt().toFloat()) {
        rounded.toInt().toString()
    } else {
        rounded.toString()
    }
}
