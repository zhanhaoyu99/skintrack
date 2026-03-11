package com.skintrack.app.ui.screen.camera

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.skintrack.app.ui.theme.extendedColors

@Composable
fun FaceGuideOverlay(modifier: Modifier = Modifier) {
    val overlayColor = MaterialTheme.extendedColors.camera.overlay
    val borderColor = MaterialTheme.extendedColors.camera.guideBorder

    Canvas(
        modifier = modifier
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

        // Oval border
        drawOval(
            color = borderColor,
            topLeft = Offset(ovalLeft, ovalTop),
            size = Size(ovalWidth, ovalHeight),
            style = Stroke(width = 2.dp.toPx()),
        )
    }
}
