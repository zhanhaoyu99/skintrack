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
import com.skintrack.app.ui.component.SkeletonCard
import com.skintrack.app.ui.component.SkeletonCircle
import com.skintrack.app.ui.component.SkeletonPill
import com.skintrack.app.ui.component.shimmer
import com.skintrack.app.ui.theme.spacing

@Composable
fun TimelineLoadingSkeleton(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing

    Column(
        modifier = modifier.padding(spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        // Filter chips row
        Row(horizontalArrangement = Arrangement.spacedBy(spacing.sm)) {
            SkeletonPill(width = 48.dp, height = 32.dp)
            SkeletonPill(width = 48.dp, height = 32.dp)
            SkeletonPill(width = 48.dp, height = 32.dp)
            SkeletonPill(width = 56.dp, height = 32.dp)
        }

        // Compare card
        SkeletonCard {
            Column(verticalArrangement = Arrangement.spacedBy(spacing.sm)) {
                // Header row: title + action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    SkeletonBox(width = 72.dp, height = 16.dp)
                    SkeletonBox(width = 48.dp, height = 14.dp)
                }

                // Two side-by-side image skeletons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(140.dp)
                            .clip(MaterialTheme.shapes.large)
                            .shimmer(),
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(140.dp)
                            .clip(MaterialTheme.shapes.large)
                            .shimmer(),
                    )
                }

                // Centered score skeleton
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    SkeletonBox(width = 80.dp, height = 24.dp)
                }
            }
        }

        // Section header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SkeletonBox(width = 72.dp, height = 16.dp)
            SkeletonBox(width = 32.dp, height = 14.dp)
        }

        // Record list card (3 items)
        SkeletonCard {
            Column {
                repeat(3) { index ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = spacing.sm),
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Thumbnail
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .shimmer(),
                        )

                        // Two text lines
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(spacing.xs),
                        ) {
                            SkeletonBox(width = 120.dp, height = 15.dp)
                            SkeletonBox(width = 160.dp, height = 12.dp)
                        }

                        // Score circle
                        SkeletonCircle(size = 46.dp)
                    }

                    if (index < 2) {
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    }
                }
            }
        }
    }
}
