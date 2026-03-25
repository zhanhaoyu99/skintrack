package com.skintrack.app.ui.screen.report

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.skintrack.app.ui.component.SkeletonBox
import com.skintrack.app.ui.component.SkeletonCard
import com.skintrack.app.ui.component.SkeletonCircle
import com.skintrack.app.ui.component.SkeletonPill
import com.skintrack.app.ui.component.shimmer
import com.skintrack.app.ui.theme.spacing

@Composable
fun RecordDetailLoadingSkeleton(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        // Photo area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceVariant,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        ),
                    ),
                ),
        ) {
            // Back button circle - top left
            SkeletonCircle(
                size = 40.dp,
                modifier = Modifier
                    .padding(start = spacing.sm, top = 54.dp)
                    .align(Alignment.TopStart),
            )
            // Bottom center pill
            SkeletonPill(
                width = 120.dp,
                height = 30.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = spacing.sm),
            )
        }

        // Score floating card - overlaps photo by -32dp
        SkeletonCard(
            modifier = Modifier
                .padding(horizontal = spacing.md)
                .offset(y = (-32).dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(spacing.listGap),
            ) {
                SkeletonCircle(size = 64.dp)
                Column(
                    verticalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    SkeletonBox(width = 140.dp, height = 18.dp, modifier = Modifier.fillMaxWidth(0.65f))
                    SkeletonBox(width = 100.dp, height = 12.dp, modifier = Modifier.fillMaxWidth(0.45f))
                    SkeletonPill(width = 80.dp, height = 20.dp)
                }
            }
        }

        // Radar chart card
        SkeletonCard(
            modifier = Modifier.padding(horizontal = spacing.md),
        ) {
            SkeletonBox(width = 70.dp, height = 14.dp)
            Spacer(modifier = Modifier.height(spacing.listGap))
            SkeletonCircle(
                size = 160.dp,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }

        Spacer(modifier = Modifier.height(spacing.listGap))

        // Score bars card
        SkeletonCard(
            modifier = Modifier.padding(horizontal = spacing.md),
        ) {
            SkeletonBox(width = 70.dp, height = 14.dp)
            Spacer(modifier = Modifier.height(spacing.md))
            Column(verticalArrangement = Arrangement.spacedBy(spacing.listGap)) {
                repeat(5) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        SkeletonBox(width = 36.dp, height = 12.dp)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .shimmer(),
                        )
                        SkeletonBox(width = 24.dp, height = 12.dp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.md))

        // AI analysis card with gradient background
        SkeletonCard(
            modifier = Modifier.padding(horizontal = spacing.md),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f),
                                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.15f),
                            ),
                        ),
                    ),
            ) {
                Column {
                    SkeletonBox(width = 72.dp, height = 16.dp)
                    Spacer(modifier = Modifier.height(spacing.sm))
                    SkeletonPill(width = 96.dp, height = 22.dp)
                    Spacer(modifier = Modifier.height(spacing.md))
                    SkeletonBox(width = 300.dp, height = 14.dp, modifier = Modifier.fillMaxWidth(1f))
                    Spacer(modifier = Modifier.height(spacing.sm))
                    SkeletonBox(width = 270.dp, height = 14.dp, modifier = Modifier.fillMaxWidth(0.9f))
                    Spacer(modifier = Modifier.height(spacing.sm))
                    SkeletonBox(width = 225.dp, height = 14.dp, modifier = Modifier.fillMaxWidth(0.75f))
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.lg))
    }
}
