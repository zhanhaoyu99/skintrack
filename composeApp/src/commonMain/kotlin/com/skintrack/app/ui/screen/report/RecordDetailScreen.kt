package com.skintrack.app.ui.screen.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.skintrack.app.domain.model.ProductCategory
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.domain.model.SkinType
import com.skintrack.app.domain.model.SkincareProduct
import com.skintrack.app.ui.component.LoadingContent
import com.skintrack.app.ui.screen.timeline.formatRecordDate
import com.skintrack.app.ui.theme.extendedColors
import com.skintrack.app.ui.theme.spacing
import org.koin.compose.viewmodel.koinViewModel

data class RecordDetailScreen(val recordId: String) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: RecordDetailViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(recordId) { viewModel.loadRecord(recordId) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("分析详情") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                        }
                    },
                )
            },
        ) { padding ->
            when (val state = uiState) {
                is RecordDetailUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center,
                    ) {
                        LoadingContent()
                    }
                }
                is RecordDetailUiState.NotFound -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "未找到该记录",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                is RecordDetailUiState.Content -> {
                    DetailContent(state, Modifier.padding(padding))
                }
            }
        }
    }
}

@Composable
private fun DetailContent(
    state: RecordDetailUiState.Content,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    val record = state.record

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = spacing.md,
            vertical = spacing.sm,
        ),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        // Score overview card
        item(key = "score_overview") {
            ScoreOverviewCard(record)
        }

        // Metric detail bars
        item(key = "metric_bars") {
            MetricBarsCard(record)
        }

        // AI summary card
        if (state.summary != null || state.recommendations.isNotEmpty()) {
            item(key = "ai_summary") {
                AiSummaryCard(state.summary, state.recommendations)
            }
        }

        // Daily products card
        item(key = "daily_products") {
            DailyProductsCard(state.usedProducts)
        }

        // Record info
        item(key = "record_info") {
            RecordInfoCard(record)
        }
    }
}

@Composable
private fun ScoreOverviewCard(record: SkinRecord) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        ) {
            Text(
                text = record.overallScore?.toString() ?: "--",
                style = MaterialTheme.typography.displayMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "综合评分",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = record.skinType.displayName(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
private fun MetricBarsCard(record: SkinRecord) {
    val skinMetric = MaterialTheme.extendedColors.skinMetric

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        ) {
            Text(
                text = "指标详情",
                style = MaterialTheme.typography.titleMedium,
            )

            if (record.overallScore != null) {
                ScoreBar(
                    label = "综合",
                    score = record.overallScore,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            if (record.poreScore != null) {
                ScoreBar(
                    label = "毛孔",
                    score = record.poreScore,
                    color = skinMetric.pore,
                )
            }
            if (record.rednessScore != null) {
                ScoreBar(
                    label = "泛红",
                    score = record.rednessScore,
                    color = skinMetric.redness,
                )
            }
            if (record.evenScore != null) {
                ScoreBar(
                    label = "均匀度",
                    score = record.evenScore,
                    color = skinMetric.evenness,
                )
            }
            if (record.blackheadDensity != null) {
                ScoreBar(
                    label = "黑头",
                    score = record.blackheadDensity,
                    color = skinMetric.hydration,
                )
            }
            if (record.acneCount != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "痘痘",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = "${record.acneCount} 个",
                        style = MaterialTheme.typography.bodyMedium,
                        color = skinMetric.acne,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun AiSummaryCard(
    summary: String?,
    recommendations: List<String>,
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        ) {
            Text(
                text = "AI 分析摘要",
                style = MaterialTheme.typography.titleMedium,
            )

            if (summary != null) {
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            if (recommendations.isNotEmpty()) {
                Text(
                    text = "建议",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.xs),
                )
                recommendations.forEach { rec ->
                    Text(
                        text = "· $rec",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyProductsCard(products: List<SkincareProduct>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        ) {
            Text(
                text = "当日使用产品",
                style = MaterialTheme.typography.titleMedium,
            )

            if (products.isEmpty()) {
                Text(
                    text = "当日无使用记录",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                products.forEach { product ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.name,
                                style = MaterialTheme.typography.bodyMedium,
                            )
                            if (product.brand != null) {
                                Text(
                                    text = product.brand,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                        Text(
                            text = product.category.displayName(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecordInfoCard(record: SkinRecord) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
        ) {
            Text(
                text = "记录信息",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "拍摄时间: ${formatRecordDate(record.recordedAt)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "肤质类型: ${record.skinType.displayName()}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun SkinType.displayName(): String = when (this) {
    SkinType.OILY -> "油性"
    SkinType.DRY -> "干性"
    SkinType.COMBINATION -> "混合"
    SkinType.SENSITIVE -> "敏感"
    SkinType.NORMAL -> "中性"
    SkinType.UNKNOWN -> "未知"
}

private fun ProductCategory.displayName(): String = when (this) {
    ProductCategory.CLEANSER -> "洁面"
    ProductCategory.TONER -> "化妆水"
    ProductCategory.SERUM -> "精华"
    ProductCategory.EMULSION -> "乳液"
    ProductCategory.CREAM -> "面霜"
    ProductCategory.SUNSCREEN -> "防晒"
    ProductCategory.MASK -> "面膜"
    ProductCategory.EYE_CREAM -> "眼霜"
    ProductCategory.EXFOLIATOR -> "去角质"
    ProductCategory.OTHER -> "其他"
}
