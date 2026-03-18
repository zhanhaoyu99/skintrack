package com.skintrack.app.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.skintrack.app.ui.component.SkeletonBox
import com.skintrack.app.ui.component.SkeletonCard
import com.skintrack.app.ui.component.SkeletonCircle
import com.skintrack.app.ui.component.SkeletonPill
import com.skintrack.app.ui.component.shimmer
import com.skintrack.app.ui.theme.spacing

@Composable
fun DashboardLoadingSkeleton(modifier: Modifier = Modifier) {
    val spacing = MaterialTheme.spacing

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        // Header skeleton
        item(key = "header") {
            HeaderSkeleton()
        }

        // Hero card skeleton
        item(key = "hero") {
            HeroSkeleton()
        }

        // Mini metrics skeleton
        item(key = "metrics") {
            MetricsSkeleton()
        }

        // Reminder card skeleton
        item(key = "reminder") {
            ReminderSkeleton()
        }

        // Quick actions 2x2 grid skeleton
        item(key = "quick_actions") {
            QuickActionsSkeleton()
        }

        // Trend chart skeleton
        item(key = "trend") {
            TrendChartSkeleton()
        }
    }
}

@Composable
private fun HeaderSkeleton() {
    val spacing = MaterialTheme.spacing

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.md + spacing.xs, vertical = spacing.md),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            SkeletonBox(width = 60.dp, height = 14.dp)
            SkeletonBox(width = 80.dp, height = 24.dp)
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            SkeletonCircle(size = 44.dp)
            SkeletonCircle(size = 44.dp)
        }
    }
}

@Composable
private fun HeroSkeleton() {
    val spacing = MaterialTheme.spacing

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.md)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFD1E8DF),
                        Color(0xFFD8DDE0),
                        Color(0xFFE2E5E8),
                    ),
                    start = androidx.compose.ui.geometry.Offset(0f, 0f),
                    end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
                ),
            )
            .height(140.dp)
            .padding(horizontal = spacing.md + spacing.xs, vertical = spacing.lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.lg),
    ) {
        SkeletonCircle(
            size = 84.dp,
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.08f),
                shape = CircleShape,
            ),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            SkeletonBox(width = 140.dp, height = 18.dp)
            SkeletonBox(width = 100.dp, height = 14.dp)
            SkeletonPill(width = 100.dp, height = 26.dp)
        }
    }
}

@Composable
private fun MetricsSkeleton() {
    val spacing = MaterialTheme.spacing

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.md),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        repeat(5) {
            SkeletonCard(
                modifier = Modifier.weight(1f),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    SkeletonBox(width = 24.dp, height = 18.dp)
                    SkeletonBox(width = 28.dp, height = 10.dp)
                }
            }
        }
    }
}

@Composable
private fun ReminderSkeleton() {
    val spacing = MaterialTheme.spacing

    SkeletonCard(
        modifier = Modifier.padding(horizontal = spacing.md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.md),
        ) {
            SkeletonBox(
                width = 44.dp,
                height = 44.dp,
                modifier = Modifier.clip(RoundedCornerShape(13.dp)),
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                SkeletonBox(width = 120.dp, height = 14.dp)
                SkeletonBox(width = 160.dp, height = 12.dp)
            }

            SkeletonPill(width = 64.dp, height = 32.dp)
        }
    }
}

@Composable
private fun QuickActionsSkeleton() {
    val spacing = MaterialTheme.spacing

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        repeat(2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            ) {
                repeat(2) {
                    SkeletonCard(
                        modifier = Modifier.weight(1f),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                        ) {
                            SkeletonBox(
                                width = 40.dp,
                                height = 40.dp,
                                modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                            )

                            Column(
                                verticalArrangement = Arrangement.spacedBy(spacing.xs),
                            ) {
                                SkeletonBox(width = 56.dp, height = 14.dp)
                                SkeletonBox(width = 40.dp, height = 10.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrendChartSkeleton() {
    val spacing = MaterialTheme.spacing

    SkeletonCard(
        modifier = Modifier.padding(horizontal = spacing.md),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SkeletonBox(width = 72.dp, height = 16.dp)
                SkeletonBox(width = 48.dp, height = 14.dp)
            }

            // Filter chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                repeat(3) {
                    SkeletonPill(width = 44.dp, height = 24.dp)
                }
            }

            // Chart area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(spacing.xs))
                    .shimmer(),
            )
        }
    }
}
