package com.skintrack.app.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.skintrack.app.ui.theme.FullRoundedShape
import com.skintrack.app.ui.theme.Motion
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.spacing

@Composable
fun ScoreBar(
    label: String,
    score: Int,
    maxScore: Int = 100,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val targetFraction = (score.toFloat() / maxScore).coerceIn(0f, 1f)
    val fraction by animateFloatAsState(
        targetValue = targetFraction,
        animationSpec = tween(
            durationMillis = Motion.LONG,
            easing = Motion.EmphasizedDecelerate,
        ),
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )

        Box(
            modifier = Modifier
                .weight(3f)
                .height(MaterialTheme.dimens.scoreBarHeight)
                .clip(FullRoundedShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .height(MaterialTheme.dimens.scoreBarHeight)
                    .clip(FullRoundedShape)
                    .background(color),
            )
        }

        Text(
            text = "$score",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
