package com.skintrack.app.ui.screen.attribution

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.skintrack.app.ui.component.animateListItem
import com.skintrack.app.ui.screen.paywall.PaywallScreen
import com.skintrack.app.ui.theme.Apricot300
import com.skintrack.app.ui.theme.Lavender300
import com.skintrack.app.ui.theme.Mint50
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
                title = { Text("归因分析报告") },
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
        // 1. Before/After score comparison
        item(key = "score_compare") {
            ScoreCompareRow(state)
        }

        // 2. Overview card with mini trend + 3 stat sub-cards
        item(key = "overview") {
            OverviewCard(state)
        }

        // 3. AI Insight card (premium only)
        if (state.isPremium && state.aiInsight.isNotEmpty()) {
            item(key = "ai_insight") {
                AiInsightCard(state.aiInsight)
            }
        }

        if (!state.isPremium) {
            item(key = "locked") {
                val navigator = LocalNavigator.currentOrThrow
                LockedFeatureCard(
                    message = FeatureGate.ATTRIBUTION_REPORT.lockedMessage,
                    onUpgrade = { navigator.push(PaywallScreen()) },
                    title = "解锁归因分析报告",
                    subtitle = "AI 智能分析 · 产品效果排行 · 改善建议",
                    tags = listOf("AI洞察", "排行榜", "改善建议"),
                )
            }
        } else if (state.attributions.isNotEmpty()) {
            // 4. Product rankings
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

            // 5. Improvement suggestions section
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
                            if (index > 0) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                    thickness = 0.5.dp,
                                )
                            }
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

// ── 1. Before/After Score Comparison ─────────────────────────────────
@Composable
private fun ScoreCompareRow(state: AttributionUiState.Content) {
    val spacing = MaterialTheme.spacing
    val functional = MaterialTheme.extendedColors.functional
    val delta = state.trendDelta
    val deltaColor = when {
        delta > 0 -> functional.success
        delta < 0 -> functional.error
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val deltaPrefix = if (delta >= 0) "+" else ""

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = spacing.lg, end = spacing.lg, top = spacing.sm, bottom = 20.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Before circle
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = CircleShape,
                    )
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${state.firstScore}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = "${state.analysisDays}天前",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 6.dp),
            )
        }

        // Arrow + delta in the middle
        Column(
            modifier = Modifier.padding(horizontal = spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            // Arrow using unicode
            Text(
                text = "\u2192",
                style = MaterialTheme.typography.titleLarge,
                color = deltaColor,
            )
            Text(
                text = "${deltaPrefix}${delta}",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = deltaColor,
                letterSpacing = (-0.5).sp,
            )
        }

        // After circle
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = CircleShape,
                        ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    )
                    .border(
                        width = 2.5.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape,
                    )
                    .background(
                        color = Mint50,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "${state.lastScore}",
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

// ── 2. Overview Card ─────────────────────────────────────────────────
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
        val successColor = functional.success
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(vertical = spacing.sm),
        ) {
            val width = size.width
            val height = size.height
            // Upward trend line
            val points = listOf(0.8f, 0.75f, 0.6f, 0.5f, 0.45f, 0.3f, 0.2f)

            // Fill area under the curve
            val fillPath = Path()
            points.forEachIndexed { index, y ->
                val x = width * index / (points.size - 1)
                val yPos = height * y
                if (index == 0) fillPath.moveTo(x, yPos) else fillPath.lineTo(x, yPos)
            }
            fillPath.lineTo(width, height)
            fillPath.lineTo(0f, height)
            fillPath.close()
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        successColor.copy(alpha = 0.12f),
                        successColor.copy(alpha = 0.01f),
                    ),
                ),
            )

            // Stroke line
            val linePath = Path()
            points.forEachIndexed { index, y ->
                val x = width * index / (points.size - 1)
                val yPos = height * y
                if (index == 0) linePath.moveTo(x, yPos) else linePath.lineTo(x, yPos)
            }
            drawPath(
                path = linePath,
                color = successColor,
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round,
                ),
            )
        }

        // 3 stat sub-cards in a Row (matching design: green gradient, mint gradient, purple gradient)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            val deltaPrefix = if (state.trendDelta >= 0) "+" else ""
            StatSubCard(
                label = "评分变化",
                value = "${deltaPrefix}${state.trendDelta}",
                backgroundBrush = Brush.linearGradient(
                    listOf(Color(0xFFECFDF5), Color(0xFFD1FAE5)),
                ),
                valueColor = functional.success,
                modifier = Modifier.weight(1f),
            )
            StatSubCard(
                label = "使用产品",
                value = "${state.productsUsed}",
                backgroundBrush = Brush.linearGradient(
                    listOf(Mint50, Color(0xFFD1F0E4)),
                ),
                valueColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f),
            )
            StatSubCard(
                label = "分析天数",
                value = "${state.analysisDays}",
                backgroundBrush = Brush.linearGradient(
                    listOf(Color(0xFFEDE9FE), Color(0xFFDDD6FE)),
                ),
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
    backgroundBrush: Brush,
    valueColor: Color,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier
            .background(
                brush = backgroundBrush,
                shape = MaterialTheme.shapes.medium,
            )
            .padding(horizontal = spacing.sm, vertical = 14.dp),
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

// ── 3. AI Insight Card ───────────────────────────────────────────────
@Composable
private fun AiInsightCard(insight: String) {
    val spacing = MaterialTheme.spacing
    val isDark = isSystemInDarkTheme()
    val aiGradient = if (isDark) {
        Brush.linearGradient(
            listOf(
                Color(0xFF153E32).copy(alpha = 0.4f),
                Color(0xFF1E1830).copy(alpha = 0.2f),
                Color(0xFF2E2418).copy(alpha = 0.15f),
            ),
        )
    } else {
        Brush.linearGradient(
            listOf(
                Color(0xFFF0FAF6), // mint tint
                Color(0xFFF5F0FF), // lavender tint
                Color(0xFFFFF8F2), // warm tint
            ),
        )
    }

    val accentGradient = Brush.horizontalGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            Lavender300,
            Apricot300,
        ),
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = aiGradient,
                shape = MaterialTheme.shapes.large,
            )
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                shape = MaterialTheme.shapes.large,
            )
            .drawBehind {
                // 3-color accent line at top (primary → lavender → apricot)
                val inset = 16.dp.toPx()
                drawRect(
                    brush = accentGradient,
                    topLeft = Offset(inset, 0f),
                    size = androidx.compose.ui.geometry.Size(
                        width = size.width - inset * 2,
                        height = 2.dp.toPx(),
                    ),
                )
            }
            .padding(spacing.md),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            // "AI 洞察" pill badge
            Text(
                text = "\u2728 AI 洞察",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(50),
                    )
                    .padding(horizontal = spacing.sm, vertical = spacing.xs),
            )

            Text(
                text = insight,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp,
            )
        }
    }
}

// ── 4. Product Attribution Item ──────────────────────────────────────
@Composable
private fun ProductAttributionItem(
    attribution: ProductAttribution,
    rank: Int,
    modifier: Modifier = Modifier,
) {
    val functional = MaterialTheme.extendedColors.functional
    val impactColor = if (attribution.impact >= 0) functional.success else functional.error
    val spacing = MaterialTheme.spacing

    // Rank badge colors matching design: gold, silver, bronze
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

    // Category-based icon colors
    val categoryName = attribution.product.category.displayName
    val (iconBg, iconColor, iconSymbol) = getCategoryIcon(categoryName)

    // Rank 1 gets highlighted card background (dark-mode-aware)
    val isDark = isSystemInDarkTheme()
    val cardContainerColor = if (rank == 1) {
        if (isDark) Color(0xFFFFC107).copy(alpha = 0.06f) else Color.Transparent
    } else {
        MaterialTheme.colorScheme.surface
    }
    val rank1Gradient = if (!isDark) {
        Brush.linearGradient(listOf(Color(0xFFFFFBEB), Color(0xFFFEF3C7)))
    } else {
        null
    }
    val cardBorderColor = if (rank == 1) {
        if (isDark) Color(0xFFFFC107).copy(alpha = 0.10f) else Color(0xFFD97706).copy(alpha = 0.12f)
    } else {
        Color.Transparent
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (rank == 1) {
                    Modifier
                        .shadow(
                            elevation = 6.dp,
                            shape = MaterialTheme.shapes.medium,
                            ambientColor = Color(0x1FF59E0B), // golden glow
                            spotColor = Color(0x14F59E0B),
                        )
                        .border(
                            width = 1.dp,
                            color = cardBorderColor,
                            shape = MaterialTheme.shapes.medium,
                        )
                } else {
                    Modifier
                },
            ),
        colors = CardDefaults.cardColors(containerColor = cardContainerColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (rank == 1 && rank1Gradient != null) {
                        Modifier.background(brush = rank1Gradient)
                    } else {
                        Modifier
                    },
                )
                .padding(horizontal = spacing.md, vertical = 14.dp),
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

            // Product category icon (40dp square with rounded corners)
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        brush = iconBg,
                        shape = RoundedCornerShape(12.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = iconSymbol,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attribution.product.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.1).sp,
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
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = impactColor,
                    letterSpacing = (-0.3).sp,
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

// ── 5. Suggestion Item ───────────────────────────────────────────────
@Composable
private fun SuggestionItem(
    number: Int,
    text: String,
) {
    val spacing = MaterialTheme.spacing

    // Gradient background per suggestion: green, purple, orange (matching design)
    val iconBgBrush = when (number) {
        1 -> Brush.linearGradient(listOf(Color(0xFFECFDF5), Color(0xFFD1FAE5)))
        2 -> Brush.linearGradient(listOf(Color(0xFFEDE9FE), Color(0xFFDDD6FE)))
        3 -> Brush.linearGradient(listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2)))
        else -> Brush.linearGradient(
            listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primaryContainer),
        )
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
                    brush = iconBgBrush,
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
            lineHeight = 20.sp,
        )
    }
}

// ── Helpers ──────────────────────────────────────────────────────────

private fun formatImpact(value: Float): String {
    val rounded = (value * 10).roundToInt() / 10f
    return if (rounded == rounded.toInt().toFloat()) {
        rounded.toInt().toString()
    } else {
        rounded.toString()
    }
}

/**
 * Returns (backgroundBrush, iconColor, emojiSymbol) for a product category.
 * Background uses gradient per design spec.
 */
private fun getCategoryIcon(categoryName: String): Triple<Brush, Color, String> {
    return when {
        categoryName.contains("洁面") -> Triple(
            Brush.linearGradient(listOf(Color(0xFFDBEAFE), Color(0xFFBFDBFE))),
            Color(0xFF2563EB), "\uD83E\uDDF4",
        )
        categoryName.contains("精华") -> Triple(
            Brush.linearGradient(listOf(Color(0xFFF3E5F5), Color(0xFFE1BEE7))),
            Color(0xFF7B1FA2), "\uD83E\uDDEA",
        )
        categoryName.contains("防晒") -> Triple(
            Brush.linearGradient(listOf(Color(0xFFFFF3E0), Color(0xFFFFE0B2))),
            Color(0xFFE65100), "\u2600\uFE0F",
        )
        categoryName.contains("面霜") || categoryName.contains("乳液") -> Triple(
            Brush.linearGradient(listOf(Color(0xFFE8F5E9), Color(0xFFC8E6C9))),
            Color(0xFF2E7D32), "\uD83C\uDF3F",
        )
        categoryName.contains("化妆水") || categoryName.contains("爽肤水") -> Triple(
            Brush.linearGradient(listOf(Color(0xFFE0F7FA), Color(0xFFB2EBF2))),
            Color(0xFF00838F), "\uD83D\uDCA7",
        )
        categoryName.contains("面膜") -> Triple(
            Brush.linearGradient(listOf(Color(0xFFFCE4EC), Color(0xFFF8BBD0))),
            Color(0xFFC62828), "\uD83C\uDFAD",
        )
        else -> Triple(
            Brush.linearGradient(listOf(Color(0xFFF5F5F5), Color(0xFFEEEEEE))),
            Color(0xFF616161), "\uD83E\uDDF4",
        )
    }
}
