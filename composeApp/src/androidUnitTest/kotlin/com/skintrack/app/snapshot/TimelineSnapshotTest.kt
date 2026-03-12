package com.skintrack.app.snapshot

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.skintrack.app.ui.component.ScoreRing
import com.skintrack.app.ui.component.animateListItem
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.spacing
import org.junit.Test

class TimelineSnapshotTest : SnapshotTestBase() {

    @Test
    fun timeline_light() = captureLight {
        TimelinePreview()
    }

    @Test
    fun timeline_dark() = captureDark {
        TimelinePreview()
    }
}

@Composable
private fun TimelinePreview() {
    val spacing = MaterialTheme.spacing

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "皮肤记录",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(horizontal = spacing.md, vertical = spacing.sm),
        )

        LazyColumn(
            contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            itemsIndexed(sampleRecords) { index, record ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateListItem(index),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(spacing.sm),
                        horizontalArrangement = Arrangement.spacedBy(spacing.md),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(spacing.xs),
                        ) {
                            Text(
                                text = record.date,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                            Text(
                                text = record.skinType,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        ScoreRing(
                            score = record.score,
                            size = MaterialTheme.dimens.thumbnailSize,
                            strokeWidth = MaterialTheme.dimens.chartDotRadius,
                            label = "",
                        )
                    }
                }
            }
        }
    }
}

private data class SampleRecord(val date: String, val score: Int, val skinType: String)

private val sampleRecords = listOf(
    SampleRecord("2026年3月12日 09:30", 82, "混合型"),
    SampleRecord("2026年3月11日 08:45", 78, "混合型"),
    SampleRecord("2026年3月10日 09:15", 75, "干性"),
    SampleRecord("2026年3月9日 10:00", 71, "混合型"),
    SampleRecord("2026年3月8日 08:30", 69, "混合型"),
)
