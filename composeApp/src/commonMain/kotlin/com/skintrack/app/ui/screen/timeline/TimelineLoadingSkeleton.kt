package com.skintrack.app.ui.screen.timeline

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.skintrack.app.ui.component.SkeletonBox
import com.skintrack.app.ui.component.SkeletonCircle
import com.skintrack.app.ui.component.SkeletonPill
import com.skintrack.app.ui.component.shimmer
import com.skintrack.app.ui.theme.spacing

@Composable
fun TimelineLoadingSkeleton(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(spacing.listGap),
    ) {
        // Filter chips row: gap=6dp, height=30dp, padding 4/16/12
        Row(
            modifier = Modifier.padding(
                start = spacing.md,
                end = spacing.md,
                top = spacing.xs,
                bottom = spacing.listGap,
            ),
            horizontalArrangement = Arrangement.spacedBy(spacing.iconGap),
        ) {
            SkeletonPill(width = 48.dp, height = 30.dp)
            SkeletonPill(width = 48.dp, height = 30.dp)
            SkeletonPill(width = 56.dp, height = 30.dp)
            SkeletonPill(width = 56.dp, height = 30.dp)
        }

        // Compare card: single 200dp shimmer box with radius-lg
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md)
                .height(200.dp)
                .clip(MaterialTheme.shapes.large)
                .shimmer(),
        )

        // Record list items (4 items matching HTML)
        Column(
            modifier = Modifier.padding(horizontal = spacing.md),
        ) {
            repeat(4) { index ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = spacing.listGap),
                    horizontalArrangement = Arrangement.spacedBy(spacing.listGap),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Thumbnail: 56dp, radius-md=12dp
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .shimmer(),
                    )

                    // Two text lines with gap=6dp
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(spacing.iconGap),
                    ) {
                        SkeletonBox(width = 120.dp, height = 14.dp)
                        SkeletonBox(width = 180.dp, height = 12.dp)
                    }

                    // Score circle: 44dp
                    SkeletonCircle(size = 44.dp)
                }

                if (index < 3) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
            }
        }
    }
}
