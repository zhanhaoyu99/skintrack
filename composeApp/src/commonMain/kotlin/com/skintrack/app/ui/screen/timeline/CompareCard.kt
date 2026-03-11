package com.skintrack.app.ui.screen.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.platform.pathToImageModel
import com.skintrack.app.ui.theme.extendedColors
import com.skintrack.app.ui.theme.spacing
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime

data class CompareData(
    val before: SkinRecord,
    val after: SkinRecord,
)

@Composable
fun CompareCard(
    data: CompareData,
    modifier: Modifier = Modifier,
) {
    val daySpan = data.before.recordedAt.daysUntil(data.after.recordedAt, TimeZone.currentSystemDefault())
    val scoreDiff = (data.after.overallScore ?: 0) - (data.before.overallScore ?: 0)

    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(MaterialTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        ) {
            // Title row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "前后对比",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "${daySpan}天",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Photo row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            ) {
                ComparePhoto(
                    record = data.before,
                    label = formatCompareDate(data.before.recordedAt),
                    modifier = Modifier.weight(1f),
                )
                ComparePhoto(
                    record = data.after,
                    label = formatCompareDate(data.after.recordedAt),
                    modifier = Modifier.weight(1f),
                )
            }

            // Score comparison row
            ScoreComparisonRow(
                beforeScore = data.before.overallScore,
                afterScore = data.after.overallScore,
                scoreDiff = scoreDiff,
            )
        }
    }
}

@Composable
private fun ScoreComparisonRow(
    beforeScore: Int?,
    afterScore: Int?,
    scoreDiff: Int,
) {
    val diffColor: Color
    val diffText: String

    when {
        scoreDiff > 0 -> {
            diffColor = MaterialTheme.extendedColors.functional.success
            diffText = "↑ +$scoreDiff"
        }
        scoreDiff < 0 -> {
            diffColor = MaterialTheme.extendedColors.functional.error
            diffText = "↓ $scoreDiff"
        }
        else -> {
            diffColor = MaterialTheme.colorScheme.onSurfaceVariant
            diffText = "± 0"
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${beforeScore ?: "-"}",
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = "之前",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Text(
            text = diffText,
            style = MaterialTheme.typography.titleMedium,
            color = diffColor,
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${afterScore ?: "-"}",
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = "之后",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ComparePhoto(
    record: SkinRecord,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
    ) {
        val imagePath = record.localImagePath
        if (imagePath != null) {
            AsyncImage(
                model = pathToImageModel(imagePath),
                contentDescription = "皮肤照片",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "无照片",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun formatCompareDate(instant: Instant): String {
    val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val month = dt.monthNumber.toString().padStart(2, '0')
    val day = dt.dayOfMonth.toString().padStart(2, '0')
    return "$month/$day"
}
