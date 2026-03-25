package com.skintrack.app.ui.screen.camera

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.skintrack.app.ui.theme.Motion
import com.skintrack.app.ui.theme.extendedColors
import com.skintrack.app.ui.theme.spacing

@Composable
fun FaceGuideOverlay(modifier: Modifier = Modifier) {
    val overlayColor = MaterialTheme.extendedColors.camera.overlay
    val borderColor = MaterialTheme.extendedColors.camera.guideBorder

    val infiniteTransition = rememberInfiniteTransition()
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(
                durationMillis = Motion.LONG,
                easing = Motion.Standard,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
    )
    val pulseStrokeWidth by infiniteTransition.animateFloat(
        initialValue = 2f,
        targetValue = 3f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(
                durationMillis = Motion.LONG,
                easing = Motion.Standard,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen),
        ) {
            val ovalWidth = size.width * 0.6f
            val ovalHeight = ovalWidth * 1.35f
            val ovalLeft = (size.width - ovalWidth) / 2
            val ovalTop = size.height * 0.15f

            // Semi-transparent background
            drawRect(overlayColor)

            // Clear oval cutout
            drawOval(
                color = Color.Black,
                topLeft = Offset(ovalLeft, ovalTop),
                size = Size(ovalWidth, ovalHeight),
                blendMode = BlendMode.Clear,
            )

            // Oval border with pulse
            drawOval(
                color = borderColor,
                topLeft = Offset(ovalLeft, ovalTop),
                size = Size(ovalWidth, ovalHeight),
                style = Stroke(width = pulseStrokeWidth.dp.toPx()),
                alpha = pulseAlpha,
            )
        }

        Text(
            text = "将面部置于框内",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.extendedColors.camera.guideBorder,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = MaterialTheme.spacing.section * 3)
                .graphicsLayer { alpha = pulseAlpha },
        )
    }
}
