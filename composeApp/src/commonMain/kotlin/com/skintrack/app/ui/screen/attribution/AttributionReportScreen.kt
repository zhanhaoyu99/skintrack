package com.skintrack.app.ui.screen.attribution

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
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
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.skintrack.app.domain.model.ProductAttribution
import com.skintrack.app.domain.model.displayName
import com.skintrack.app.ui.component.LoadingContent
import com.skintrack.app.ui.component.SectionCard
import com.skintrack.app.ui.component.SectionHeader
import com.skintrack.app.ui.component.TrendIndicator
import com.skintrack.app.ui.component.animateListItem
import com.skintrack.app.ui.theme.extendedColors
import com.skintrack.app.ui.theme.spacing
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.abs
import kotlin.math.roundToInt

class AttributionReportScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: AttributionReportViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsState()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("归因分析报告") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
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
        item(key = "summary") {
            SummaryCard(state)
        }

        item(key = "trend") {
            TrendCard(state)
        }

        if (state.attributions.isNotEmpty()) {
            item(key = "ranking_header") {
                Text(
                    text = "产品影响排行",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = spacing.sm),
                )
            }
            itemsIndexed(state.attributions, key = { _, it -> it.product.id }) { index, attribution ->
                ProductAttributionItem(attribution, Modifier.animateListItem(index))
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
private fun SummaryCard(state: AttributionUiState.Content) {
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
                text = "${state.totalRecords} 条",
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
                text = state.dateRange,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
            )
        }
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
    modifier: Modifier = Modifier,
) {
    val functional = MaterialTheme.extendedColors.functional
    val impactColor = if (attribution.impact >= 0) functional.success else functional.error
    val impactLabel = if (attribution.impact >= 0) "有益" else "有害"

    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.md),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attribution.product.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                ) {
                    Text(
                        text = attribution.product.category.displayName,
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
                val prefix = if (attribution.impact >= 0) "+" else ""
                Text(
                    text = "${prefix}${formatImpact(attribution.impact)}",
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
                            horizontal = MaterialTheme.spacing.sm,
                            vertical = MaterialTheme.spacing.xs,
                        ),
                )
            }
        }
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

