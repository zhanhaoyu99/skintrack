package com.skintrack.app.ui.screen.timeline

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.spacing
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class ChartRecord(
    val date: Instant,
    val overallScore: Int,
    val acneCount: Int? = null,
    val poreScore: Int? = null,
    val evenScore: Int? = null,
    val hydrationScore: Int? = null,
)

enum class ChartMetric(val label: String) {
    OVERALL("总评分"),
    ACNE("痘痘"),
    PORE("毛孔"),
    EVEN("均匀度"),
    HYDRATION("水润"),
}

fun ChartRecord.scoreFor(metric: ChartMetric): Int? = when (metric) {
    ChartMetric.OVERALL -> overallScore
    ChartMetric.ACNE -> acneCount
    ChartMetric.PORE -> poreScore
    ChartMetric.EVEN -> evenScore
    ChartMetric.HYDRATION -> hydrationScore
}

@Composable
fun TrendChart(
    points: List<ChartRecord>,
    metric: ChartMetric = ChartMetric.OVERALL,
    modifier: Modifier = Modifier,
) {
    val filteredPoints = remember(points, metric) {
        points.filter { it.scoreFor(metric) != null }
    }
    if (filteredPoints.size < 2) return

    val lineColor = MaterialTheme.colorScheme.primary
    val fillColor = lineColor.copy(alpha = 0.1f)
    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val labelStyle = MaterialTheme.typography.labelSmall.copy(color = labelColor)
    val textMeasurer = rememberTextMeasurer()
    val chartHeightDp = MaterialTheme.dimens.chartHeight
    val chartLineWidthDp = MaterialTheme.dimens.chartLineWidth
    val chartDotRadiusDp = MaterialTheme.dimens.chartDotRadius

    val maxScore = if (metric == ChartMetric.ACNE) {
        (filteredPoints.maxOfOrNull { it.scoreFor(metric) ?: 0 } ?: 20).coerceAtLeast(10)
    } else 100

    val yLabels = remember(maxScore) {
        listOf("$maxScore", "${maxScore / 2}", "0")
    }
    val xLabels = remember(filteredPoints) {
        listOf(
            formatChartDate(filteredPoints.first().date),
            formatChartDate(filteredPoints.last().date),
        )
    }

    // Pre-measure label sizes
    val yLabelLayouts = remember(yLabels, labelStyle) {
        yLabels.map { textMeasurer.measure(it, labelStyle) }
    }
    val xLabelLayouts = remember(xLabels, labelStyle) {
        xLabels.map { textMeasurer.measure(it, labelStyle) }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(chartHeightDp),
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
            val coords = filteredPoints.mapIndexed { i, point ->
                val score = point.scoreFor(metric) ?: 0
                val x = if (filteredPoints.size == 1) chartWidth / 2
                else paddingLeft + chartWidth * i / (filteredPoints.size - 1).toFloat()
                val y = chartHeight * (1f - score.toFloat() / maxScore)
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
                style = Stroke(width = chartLineWidthDp.toPx()),
            )

            // Draw data point circles
            coords.forEachIndexed { i, offset ->
                if (i == coords.lastIndex) {
                    // Last point: larger white circle with primary border + shadow
                    drawCircle(
                        color = lineColor.copy(alpha = 0.2f),
                        radius = 9.dp.toPx(),
                        center = offset,
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 7.dp.toPx(),
                        center = offset,
                    )
                    drawCircle(
                        color = lineColor,
                        radius = 7.dp.toPx(),
                        center = offset,
                        style = Stroke(width = 2.dp.toPx()),
                    )

                    // Tooltip above last point
                    val lastScore = filteredPoints.last().scoreFor(metric) ?: 0
                    val tooltipText = if (metric == ChartMetric.ACNE) "$lastScore 个" else "$lastScore 分"
                    val tooltipLayout = textMeasurer.measure(
                        tooltipText,
                        TextStyle(
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    val tooltipPaddingH = 10.dp.toPx()
                    val tooltipPaddingV = 5.dp.toPx()
                    val tooltipWidth = tooltipLayout.size.width + tooltipPaddingH * 2
                    val tooltipHeight = tooltipLayout.size.height + tooltipPaddingV * 2
                    val tooltipX = (offset.x - tooltipWidth / 2).coerceIn(
                        paddingLeft,
                        paddingLeft + chartWidth - tooltipWidth,
                    )
                    val tooltipY = offset.y - tooltipHeight - 12.dp.toPx()
                    val cornerRadius = CornerRadius(6.dp.toPx())
                    drawRoundRect(
                        color = lineColor,
                        topLeft = Offset(tooltipX, tooltipY),
                        size = Size(tooltipWidth, tooltipHeight),
                        cornerRadius = cornerRadius,
                    )
                    drawText(
                        textLayoutResult = tooltipLayout,
                        topLeft = Offset(
                            tooltipX + tooltipPaddingH,
                            tooltipY + tooltipPaddingV,
                        ),
                    )
                } else {
                    drawCircle(
                        color = lineColor,
                        radius = chartDotRadiusDp.toPx(),
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
