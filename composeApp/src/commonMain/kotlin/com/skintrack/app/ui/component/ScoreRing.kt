package com.skintrack.app.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skintrack.app.ui.theme.Motion
import com.skintrack.app.ui.theme.gradients

@Composable
fun ScoreRing(
    score: Int,
    maxScore: Int = 100,
    label: String = "总评分",
    size: Dp = 160.dp,
    strokeWidth: Dp = 10.dp,
    scoreColor: Color? = null,
    modifier: Modifier = Modifier,
) {
    val fraction = (score.toFloat() / maxScore).coerceIn(0f, 1f)
    val animatedFraction by animateFloatAsState(
        targetValue = fraction,
        animationSpec = tween(
            durationMillis = Motion.LONG,
            easing = Motion.EmphasizedDecelerate,
        ),
    )

    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val gradientBrush = MaterialTheme.gradients.scoreRing

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val stroke = Stroke(
                width = strokeWidth.toPx(),
                cap = StrokeCap.Round,
            )

            // Background track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke,
            )

            // Animated progress arc with gradient
            drawArc(
                brush = gradientBrush,
                startAngle = -90f,
                sweepAngle = 360f * animatedFraction,
                useCenter = false,
                style = stroke,
            )
        }

        // Adapt text size based on ring size
        val isCompact = size <= 100.dp
        val isMini = size <= 50.dp
        val resolvedScoreColor = scoreColor ?: MaterialTheme.colorScheme.onSurface
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = score.toString(),
                style = when {
                    isMini -> MaterialTheme.typography.labelLarge.copy(
                        letterSpacing = (-0.3).sp,
                    )
                    isCompact -> MaterialTheme.typography.titleLarge.copy(
                        fontSize = 24.sp,
                        letterSpacing = (-0.5).sp,
                    )
                    else -> MaterialTheme.typography.displayMedium
                },
                fontWeight = FontWeight.ExtraBold,
                color = resolvedScoreColor,
            )
            if (!isCompact) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
