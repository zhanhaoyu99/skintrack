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
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.platform.pathToImageModel
import com.skintrack.app.ui.component.EmptyContent
import com.skintrack.app.ui.component.LoadingContent
import com.skintrack.app.ui.theme.spacing
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun TimelineScreen(
    viewModel: TimelineViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

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
                    items(state.records, key = { it.id }) { record ->
                        TimelineRecordItem(record)
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineRecordItem(record: SkinRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
                        .size(72.dp)
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
                val scoreText = if (record.overallScore != null) {
                    "综合评分: ${record.overallScore}"
                } else {
                    "待分析"
                }
                Text(
                    text = scoreText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
