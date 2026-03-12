package com.skintrack.app.ui.screen.share

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil3.compose.AsyncImage
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.platform.pathToImageModel
import com.skintrack.app.ui.component.TrendIndicator
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.gradients
import com.skintrack.app.ui.theme.spacing
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@Composable
fun ShareCardContent(
    before: SkinRecord,
    after: SkinRecord,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    val scoreDiff = (after.overallScore ?: 0) - (before.overallScore ?: 0)

    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Brand header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.gradients.primary)
                    .padding(vertical = spacing.md),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "SkinTrack",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }

            // Comparison photos
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.md),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                SharePhoto(
                    record = before,
                    label = "之前 · ${formatShareDate(before.recordedAt)}",
                    modifier = Modifier.weight(1f),
                )
                SharePhoto(
                    record = after,
                    label = "之后 · ${formatShareDate(after.recordedAt)}",
                    modifier = Modifier.weight(1f),
                )
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = spacing.md))

            // Score change
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.md),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "评分变化  ",
                    style = MaterialTheme.typography.bodyMedium,
                )
                TrendIndicator(value = scoreDiff)
                Text(
                    text = "  ${before.overallScore ?: "-"} → ${after.overallScore ?: "-"}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = spacing.md))

            // Watermark
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.sm),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "SkinTrack · 记录你的皮肤变化",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun SharePhoto(
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
                    .height(MaterialTheme.dimens.photoCompareHeight)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.dimens.photoCompareHeight)
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

private fun formatShareDate(instant: Instant): String {
    val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val month = dt.monthNumber.toString().padStart(2, '0')
    val day = dt.dayOfMonth.toString().padStart(2, '0')
    return "$month/$day"
}
