package com.skintrack.app.ui.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import com.skintrack.app.ui.theme.Motion
import com.skintrack.app.ui.theme.spacing
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

data class RadarMetric(
    val label: String,
    val value: Float,
    val maxValue: Float = 100f,
    val color: Color,
)

@Composable
fun RadarChart(
    metrics: List<RadarMetric>,
    modifier: Modifier = Modifier,
    fillColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
    fillBrush: Brush? = null,
    strokeColor: Color = MaterialTheme.colorScheme.primary,
    gridColor: Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
    gridLevels: Int = 4,
) {
    require(metrics.size >= 3) { "RadarChart requires at least 3 metrics" }

    val animatedValues = metrics.map { metric ->
        val fraction = (metric.value / metric.maxValue).coerceIn(0f, 1f)
        val animated by animateFloatAsState(
            targetValue = fraction,
            animationSpec = tween(
                durationMillis = Motion.LONG,
                easing = Motion.EmphasizedDecelerate,
            ),
        )
        animated
    }

    val textMeasurer = rememberTextMeasurer()
    val labelStyle = MaterialTheme.typography.labelSmall
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val valueStyle = MaterialTheme.typography.labelSmall.copy(
        color = MaterialTheme.colorScheme.onSurface,
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.xl),
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = min(size.width, size.height) / 2f

            // Draw grid levels (concentric polygons)
            for (level in 1..gridLevels) {
                val levelRadius = radius * level / gridLevels
                drawPolygon(
                    center = center,
                    radius = levelRadius,
                    sides = metrics.size,
                    color = gridColor,
                    style = Stroke(width = 1.dp.toPx()),
                )
            }

            // Draw axis lines from center to each vertex
            val angleStep = 2 * PI / metrics.size
            for (i in metrics.indices) {
                val angle = -PI / 2 + i * angleStep
                val endX = center.x + radius * cos(angle).toFloat()
                val endY = center.y + radius * sin(angle).toFloat()
                drawLine(
                    color = gridColor,
                    start = center,
                    end = Offset(endX, endY),
                    strokeWidth = 1.dp.toPx(),
                )
            }

            // Draw data polygon (filled)
            val dataPath = Path()
            for (i in metrics.indices) {
                val angle = -PI / 2 + i * angleStep
                val valueRadius = radius * animatedValues[i]
                val x = center.x + valueRadius * cos(angle).toFloat()
                val y = center.y + valueRadius * sin(angle).toFloat()
                if (i == 0) dataPath.moveTo(x, y) else dataPath.lineTo(x, y)
            }
            dataPath.close()

            if (fillBrush != null) {
                drawPath(path = dataPath, brush = fillBrush, style = Fill)
            } else {
                drawPath(path = dataPath, color = fillColor, style = Fill)
            }
            drawPath(
                path = dataPath,
                color = strokeColor,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
            )

            // Draw data points (dots on vertices)
            for (i in metrics.indices) {
                val angle = -PI / 2 + i * angleStep
                val valueRadius = radius * animatedValues[i]
                val x = center.x + valueRadius * cos(angle).toFloat()
                val y = center.y + valueRadius * sin(angle).toFloat()

                // Outer circle (metric color)
                drawCircle(
                    color = metrics[i].color,
                    radius = 5.dp.toPx(),
                    center = Offset(x, y),
                )
                // Inner circle (white dot)
                drawCircle(
                    color = Color.White,
                    radius = 2.5.dp.toPx(),
                    center = Offset(x, y),
                )
            }

            // Draw labels
            val labelPadding = 8.dp.toPx()
            for (i in metrics.indices) {
                val angle = -PI / 2 + i * angleStep
                val labelRadius = radius + labelPadding
                val labelX = center.x + labelRadius * cos(angle).toFloat()
                val labelY = center.y + labelRadius * sin(angle).toFloat()

                val label = metrics[i].label
                val valueText = "${metrics[i].value.toInt()}"
                val fullText = "$label $valueText"

                drawLabel(
                    textMeasurer = textMeasurer,
                    label = label,
                    value = valueText,
                    labelStyle = labelStyle.copy(color = labelColor),
                    valueStyle = valueStyle.copy(color = metrics[i].color),
                    position = Offset(labelX, labelY),
                    angle = angle,
                )
            }
        }
    }
}

private fun DrawScope.drawLabel(
    textMeasurer: TextMeasurer,
    label: String,
    value: String,
    labelStyle: TextStyle,
    valueStyle: TextStyle,
    position: Offset,
    angle: Double,
) {
    val labelResult = textMeasurer.measure(label, labelStyle)
    val valueResult = textMeasurer.measure(value, valueStyle)

    val totalWidth = labelResult.size.width + 2.dp.toPx() + valueResult.size.width
    val maxHeight = maxOf(labelResult.size.height, valueResult.size.height)

    // Determine horizontal alignment based on angle position
    val normalizedAngle = ((angle + PI / 2) % (2 * PI) + 2 * PI) % (2 * PI)
    val offsetX = when {
        normalizedAngle < PI * 0.15 || normalizedAngle > PI * 1.85 -> -totalWidth / 2f // top center
        normalizedAngle < PI -> 0f // right side
        else -> -totalWidth // left side
    }
    val offsetY = when {
        normalizedAngle > PI * 0.85 && normalizedAngle < PI * 1.15 -> 0f // bottom
        normalizedAngle < PI * 0.15 || normalizedAngle > PI * 1.85 -> -maxHeight.toFloat() // top
        else -> -maxHeight / 2f // middle
    }

    val startX = position.x + offsetX
    val startY = position.y + offsetY

    drawText(
        textLayoutResult = labelResult,
        topLeft = Offset(startX, startY),
    )
    drawText(
        textLayoutResult = valueResult,
        topLeft = Offset(startX + labelResult.size.width + 2.dp.toPx(), startY),
    )
}

private fun DrawScope.drawPolygon(
    center: Offset,
    radius: Float,
    sides: Int,
    color: Color,
    style: Stroke,
) {
    val path = Path()
    val angleStep = 2 * PI / sides
    for (i in 0 until sides) {
        val angle = -PI / 2 + i * angleStep
        val x = center.x + radius * cos(angle).toFloat()
        val y = center.y + radius * sin(angle).toFloat()
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path = path, color = color, style = style)
}
