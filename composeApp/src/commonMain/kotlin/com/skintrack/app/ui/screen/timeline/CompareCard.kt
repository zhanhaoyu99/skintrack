package com.skintrack.app.ui.screen.timeline

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.platform.pathToImageModel
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.gradients
import com.skintrack.app.ui.theme.spacing
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone

import kotlinx.datetime.toLocalDateTime

import androidx.compose.material3.TextButton

data class CompareData(
    val before: SkinRecord,
    val after: SkinRecord,
)

@Composable
fun CompareCard(
    data: CompareData,
    modifier: Modifier = Modifier,
    onShare: (() -> Unit)? = null,
) {
    val scoreDiff = (data.after.overallScore ?: 0) - (data.before.overallScore ?: 0)

    Card(modifier = modifier) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.gradients.warm)
                .padding(MaterialTheme.spacing.md),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.listGap),
        ) {
            // Title row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "前后对比",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                )
                if (onShare != null) {
                    TextButton(onClick = onShare) {
                        Text(
                            text = "分享",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            // Photo row with centered VS badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.listGap),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ComparePhoto(
                    record = data.before,
                    label = formatCompareDate(data.before.recordedAt),
                    modifier = Modifier.weight(1f),
                )

                // VS badge: 32dp circle, c1 size
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "VS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.2.sp,
                    )
                }

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
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Delta score: num-lg (26px/700)
        val diffText = if (scoreDiff >= 0) "+$scoreDiff 分" else "$scoreDiff 分"
        val diffColor = when {
            scoreDiff > 0 -> Color(0xFF16A34A) // success
            scoreDiff < 0 -> MaterialTheme.colorScheme.error
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }
        Text(
            text = diffText,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = diffColor,
            letterSpacing = (-0.5).sp,
        )
        // Description: b3 (13px)
        Text(
            text = if (scoreDiff > 0) "皮肤在持续变好哦，继续加油~" else "继续坚持护肤习惯吧~",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.05.sp,
        )
    }
}

@Composable
private fun ComparePhoto(
    record: SkinRecord,
    label: String,
    modifier: Modifier = Modifier,
) {
    // Photo with date overlay at bottom
    Box(
        modifier = modifier
            .height(MaterialTheme.dimens.photoCompareHeight)
            .clip(MaterialTheme.shapes.medium), // radius-md = 12dp
    ) {
        val imagePath = record.localImagePath
        if (imagePath != null) {
            AsyncImage(
                model = pathToImageModel(imagePath),
                contentDescription = "皮肤照片",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop,
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "无照片",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        // Date overlay at bottom (c2: 10px/600)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)),
                    ),
                )
                .padding(horizontal = 8.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                letterSpacing = 0.3.sp,
            )
        }
    }
}

private fun formatCompareDate(instant: Instant): String {
    val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val month = dt.monthNumber.toString().padStart(2, '0')
    val day = dt.dayOfMonth.toString().padStart(2, '0')
    return "$month/$day"
}
