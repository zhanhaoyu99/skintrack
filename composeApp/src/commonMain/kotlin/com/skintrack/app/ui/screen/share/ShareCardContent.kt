package com.skintrack.app.ui.screen.share

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.platform.pathToImageModel
import com.skintrack.app.ui.theme.Primary50
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.extendedColors
import com.skintrack.app.ui.theme.gradients
import com.skintrack.app.ui.theme.spacing
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime

@Composable
fun ShareCardContent(
    before: SkinRecord,
    after: SkinRecord,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    val scoreDiff = (after.overallScore ?: 0) - (before.overallScore ?: 0)
    val functional = MaterialTheme.extendedColors.functional

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 24.dp,
                shape = MaterialTheme.shapes.extraLarge,
                ambientColor = Color.Black.copy(alpha = 0.12f),
                spotColor = Color.Black.copy(alpha = 0.05f),
            )
            .border(
                width = 0.5.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f),
                shape = MaterialTheme.shapes.extraLarge,
            ),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Brand header with hero gradient + logo + app name + decorative circles
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.gradients.hero)
                    .padding(horizontal = spacing.md, vertical = spacing.listGap),
            ) {
                // Decorative circle
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 20.dp, y = (-20).dp)
                        .size(60.dp)
                        .background(
                            color = Color.White.copy(alpha = 0.06f),
                            shape = CircleShape,
                        ),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    // Logo icon (24dp rounded square)
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.18f),
                                shape = MaterialTheme.shapes.extraSmall,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "\uD83C\uDF3F",
                            fontSize = 14.sp,
                        )
                    }
                    Text(
                        text = "SkinTrack",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        text = "变美日记",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f),
                    )
                }
            }

            // Comparison photos with VS badge
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.md),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    SharePhoto(
                        record = before,
                        label = "之前 \u00B7 ${formatShareDate(before.recordedAt)}",
                        modifier = Modifier.weight(1f),
                    )
                    SharePhoto(
                        record = after,
                        label = "之后 \u00B7 ${formatShareDate(after.recordedAt)}",
                        modifier = Modifier.weight(1f),
                    )
                }
                // VS badge (34dp white circle with chevron) centered between photos
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .zIndex(1f)
                        .size(34.dp)
                        .background(
                            color = Color.White,
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "\u203A",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = spacing.md))

            // Score change result section (44sp delta + description + period pill)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                val prefix = if (scoreDiff >= 0) "+" else ""
                val scoreColor = when {
                    scoreDiff > 0 -> functional.success
                    scoreDiff < 0 -> functional.error
                    else -> MaterialTheme.colorScheme.onSurface
                }
                // Large delta with arrow and "分" suffix
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    if (scoreDiff > 0) {
                        Text(
                            text = "\u2191",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = scoreColor,
                        )
                    }
                    Text(
                        text = "${prefix}${scoreDiff} 分",
                        fontSize = 44.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = scoreColor,
                        letterSpacing = (-1.5).sp,
                    )
                }
                // Description with days
                val beforeDate = before.recordedAt.toLocalDateTime(TimeZone.currentSystemDefault())
                val afterDate = after.recordedAt.toLocalDateTime(TimeZone.currentSystemDefault())
                val daysBetween = beforeDate.date.daysUntil(afterDate.date)
                Text(
                    text = "${daysBetween} 天变美日记~",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                // Time range pill
                Text(
                    text = "\uD83D\uDCC5 ${formatFullDate(before.recordedAt)} - ${formatFullDate(after.recordedAt)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = com.skintrack.app.ui.theme.FullRoundedShape,
                        )
                        .padding(horizontal = spacing.listGap, vertical = spacing.xs),
                )
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = spacing.md))

            // Watermark: left text + right QR placeholder
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.md, vertical = spacing.compact),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = "使用 SkinTrack",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "见证每一天的美好变化",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                // QR placeholder
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = MaterialTheme.shapes.extraSmall,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "QR",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
internal fun TemplateSelector(
    onUnavailableClick: () -> Unit = {},
) {
    val spacing = MaterialTheme.spacing

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.listGap, Alignment.CenterHorizontally),
    ) {
        repeat(3) { index ->
            val isSelected = index == 0
            Box(
                modifier = Modifier
                    .then(
                        if (isSelected) {
                            // Outer glow ring: 3dp padding with primary tinted background
                            Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                    shape = MaterialTheme.shapes.medium,
                                )
                                .padding(3.dp)
                        } else {
                            Modifier
                        },
                    )
                    .size(56.dp)
                    .background(
                        color = if (isSelected) Primary50
                        else MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.medium,
                    )
                    .border(
                        width = if (isSelected) 2.dp else 1.5.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant,
                        shape = MaterialTheme.shapes.medium,
                    )
                    .then(
                        if (!isSelected) {
                            Modifier.clickable { onUnavailableClick() }
                        } else {
                            Modifier
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                // Template icons (simplified)
                val icon = when (index) {
                    0 -> "\u2501" // horizontal line (comparison)
                    1 -> "\u25CB" // circle (single)
                    2 -> "\u253C" // cross (grid)
                    else -> ""
                }
                Text(
                    text = icon,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
internal fun ShareTargets(
    onTargetClick: () -> Unit,
) {
    val spacing = MaterialTheme.spacing
    // Design: light backgrounds with colored icons
    data class ShareTarget(
        val name: String,
        val emoji: String,
        val bgColor: Color,
        val iconColor: Color,
    )
    val targets = listOf(
        ShareTarget("微信", "\u5FAE", Color(0xFFE8F5E9), Color(0xFF4CAF50)),
        ShareTarget("微博", "\u535A", Color(0xFFFFF3E0), Color(0xFFFF6D00)),
        ShareTarget("小红书", "\u7EA2", Color(0xFFE3F2FD), Color(0xFF1976D2)),
        ShareTarget(
            "更多", "\u00B7\u00B7\u00B7",
            MaterialTheme.colorScheme.surfaceVariant,
            MaterialTheme.colorScheme.onSurfaceVariant,
        ),
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        targets.forEach { target ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.iconGap),
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = target.bgColor,
                            shape = CircleShape,
                        )
                        .clickable(onClick = onTargetClick),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = target.emoji,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = target.iconColor,
                    )
                }
                Text(
                    text = target.name,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Medium,
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

private fun formatFullDate(instant: Instant): String {
    val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dt.year}.${dt.monthNumber}.${dt.dayOfMonth}"
}
