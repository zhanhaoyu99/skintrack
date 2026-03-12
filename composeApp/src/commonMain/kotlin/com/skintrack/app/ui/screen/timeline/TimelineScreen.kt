package com.skintrack.app.ui.screen.timeline

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
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.domain.model.displayName
import com.skintrack.app.platform.pathToImageModel
import com.skintrack.app.ui.component.EmptyContent
import com.skintrack.app.ui.component.LoadingContent
import com.skintrack.app.ui.component.ScoreRing
import com.skintrack.app.ui.component.animateListItem
import com.skintrack.app.ui.screen.attribution.AttributionReportScreen
import com.skintrack.app.ui.screen.report.RecordDetailScreen
import com.skintrack.app.ui.screen.share.ShareCardScreen
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.extendedColors
import com.skintrack.app.ui.theme.spacing
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigator = LocalNavigator.currentOrThrow

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "皮肤记录",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(
                horizontal = MaterialTheme.spacing.md,
                vertical = MaterialTheme.spacing.sm,
            ),
        )

        when (val state = uiState) {
            is TimelineUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    LoadingContent()
                }
            }
            is TimelineUiState.Empty -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    EmptyContent(message = "还没有记录，去拍一张吧")
                }
            }
            is TimelineUiState.Content -> {
                LazyColumn(
                    contentPadding = PaddingValues(
                        horizontal = MaterialTheme.spacing.md,
                        vertical = MaterialTheme.spacing.sm,
                    ),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                ) {
                    if (state.chartPoints.size >= 2) {
                        item(key = "trend_chart") {
                            TrendChart(
                                points = state.chartPoints,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = MaterialTheme.spacing.md),
                            )
                        }
                    }
                    if (state.compareData != null) {
                        item(key = "compare_card") {
                            CompareCard(
                                data = state.compareData,
                                onShare = {
                                    navigator.push(
                                        ShareCardScreen(
                                            beforeId = state.compareData.before.id,
                                            afterId = state.compareData.after.id,
                                        )
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = MaterialTheme.spacing.md),
                            )
                        }
                    }
                    if (state.records.size >= 3) {
                        item(key = "attribution_entry") {
                            AttributionEntryCard(
                                onClick = { navigator.push(AttributionReportScreen()) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = MaterialTheme.spacing.md),
                            )
                        }
                    }
                    itemsIndexed(state.records, key = { _, it -> it.id }) { index, record ->
                        TimelineRecordItem(
                            record = record,
                            onClick = { navigator.push(RecordDetailScreen(record.id)) },
                            modifier = Modifier.animateListItem(index),
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimelineRecordItem(
    record: SkinRecord,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val imagePath = record.localImagePath
            if (imagePath != null) {
                AsyncImage(
                    model = pathToImageModel(imagePath),
                    contentDescription = "皮肤照片",
                    modifier = Modifier
                        .size(MaterialTheme.dimens.thumbnailSize)
                        .clip(MaterialTheme.shapes.small),
                    contentScale = ContentScale.Crop,
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
            ) {
                Text(
                    text = formatRecordDate(record.recordedAt),
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = record.skinType.displayName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            if (record.overallScore != null) {
                ScoreRing(
                    score = record.overallScore,
                    size = MaterialTheme.dimens.thumbnailSize,
                    strokeWidth = MaterialTheme.dimens.chartDotRadius,
                    label = "",
                )
            } else {
                Text(
                    text = "待分析",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttributionEntryCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        onClick = onClick,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
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
