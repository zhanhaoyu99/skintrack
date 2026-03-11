package com.skintrack.app.ui.screen.timeline

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skintrack.app.ui.theme.spacing
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class ChartRecord(
    val date: Instant,
    val overallScore: Int,
)

@Composable
fun TrendChart(
    points: List<ChartRecord>,
    modifier: Modifier = Modifier,
) {
    val lineColor = MaterialTheme.colorScheme.primary
    val fillColor = lineColor.copy(alpha = 0.1f)
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val labelStyle = TextStyle(fontSize = 11.sp, color = labelColor)
    val textMeasurer = rememberTextMeasurer()

    val yLabels = remember { listOf("100", "50", "0") }
    val xLabels = remember(points) {
        listOf(
            formatChartDate(points.first().date),
            formatChartDate(points.last().date),
        )
    }

    // Pre-measure label sizes
    val yLabelLayouts = remember(yLabels, labelStyle) {
        yLabels.map { textMeasurer.measure(it, labelStyle) }
    }
    val xLabelLayouts = remember(xLabels, labelStyle) {
        xLabels.map { textMeasurer.measure(it, labelStyle) }
    }

    Card(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(MaterialTheme.spacing.md),
        ) {
            val yLabelWidth = yLabelLayouts.maxOf { it.size.width }.toFloat()
            val xLabelHeight = xLabelLayouts.maxOf { it.size.height }.toFloat()
            val paddingLeft = yLabelWidth + 8.dp.toPx()
            val paddingBottom = xLabelHeight + 6.dp.toPx()
            val chartWidth = size.width - paddingLeft
            val chartHeight = size.height - paddingBottom

            // Draw Y-axis labels: 100, 50, 0
            val yPositions = listOf(0f, chartHeight / 2, chartHeight)
            yLabels.forEachIndexed { i, _ ->
                val layout = yLabelLayouts[i]
                drawText(
                    textLayoutResult = layout,
                    topLeft = Offset(
                        x = yLabelWidth - layout.size.width,
                        y = yPositions[i] - layout.size.height / 2f,
                    ),
                )
            }

            // Draw X-axis labels: first and last date
            drawText(
                textLayoutResult = xLabelLayouts[0],
                topLeft = Offset(
                    x = paddingLeft,
                    y = chartHeight + 6.dp.toPx(),
                ),
            )
            drawText(
                textLayoutResult = xLabelLayouts[1],
                topLeft = Offset(
                    x = paddingLeft + chartWidth - xLabelLayouts[1].size.width,
                    y = chartHeight + 6.dp.toPx(),
                ),
            )

            // Map data points to canvas coordinates
            val coords = points.mapIndexed { i, point ->
                val x = if (points.size == 1) chartWidth / 2
                else paddingLeft + chartWidth * i / (points.size - 1).toFloat()
                val y = chartHeight * (1f - point.overallScore / 100f)
                Offset(x, y)
            }

            // Build line path
            val linePath = Path().apply {
                coords.forEachIndexed { i, offset ->
                    if (i == 0) moveTo(offset.x, offset.y)
                    else lineTo(offset.x, offset.y)
                }
            }

            // Draw filled area under the line
            val fillPath = Path().apply {
                addPath(linePath)
                lineTo(coords.last().x, chartHeight)
                lineTo(coords.first().x, chartHeight)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(fillColor, Color.Transparent),
                    startY = 0f,
                    endY = chartHeight,
                ),
            )

            // Draw line
            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(width = 2.dp.toPx()),
            )

            // Draw data point circles
            coords.forEach { offset ->
                drawCircle(
                    color = lineColor,
                    radius = 4.dp.toPx(),
                    center = offset,
                )
            }
        }
    }
}

private fun formatChartDate(instant: Instant): String {
    val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val month = dt.monthNumber.toString().padStart(2, '0')
    val day = dt.dayOfMonth.toString().padStart(2, '0')
    return "$month/$day"
}
