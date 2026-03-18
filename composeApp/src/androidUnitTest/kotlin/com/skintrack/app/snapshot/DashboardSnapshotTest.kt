package com.skintrack.app.snapshot

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skintrack.app.ui.component.ScoreRing
import com.skintrack.app.ui.component.SectionCard
import com.skintrack.app.ui.component.SectionHeader
import com.skintrack.app.ui.theme.Apricot100
import com.skintrack.app.ui.theme.Apricot50
import com.skintrack.app.ui.theme.FullRoundedShape
import com.skintrack.app.ui.theme.Lavender100
import com.skintrack.app.ui.theme.Lavender200
import com.skintrack.app.ui.theme.Lavender50
import com.skintrack.app.ui.theme.Mint100
import com.skintrack.app.ui.theme.Mint200
import com.skintrack.app.ui.theme.Mint50
import com.skintrack.app.ui.theme.Rose100
import com.skintrack.app.ui.theme.Rose200
import com.skintrack.app.ui.theme.Rose300
import com.skintrack.app.ui.theme.Rose400
import com.skintrack.app.ui.theme.Rose50
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.extendedColors
import com.skintrack.app.ui.theme.gradients
import com.skintrack.app.ui.theme.spacing
import org.junit.Test

class DashboardSnapshotTest : SnapshotTestBase() {

    @Test
    fun dashboard_empty_light() = captureLight {
        DashboardEmptyPreview()
    }

    @Test
    fun dashboard_empty_dark() = captureDark {
        DashboardEmptyPreview()
    }

    @Test
    fun dashboard_content_light() = captureLight {
        DashboardContentPreview()
    }

    @Test
    fun dashboard_content_dark() = captureDark {
        DashboardContentPreview()
    }
}

// region Header

@Composable
private fun DashboardHeaderPreview(username: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "上午好 \u2600\uFE0F",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                letterSpacing = 0.1.sp,
            )
            Text(
                text = username,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.6).sp,
                modifier = Modifier.padding(top = 2.dp),
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Notification bell in surface circle
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(0.5.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "通知",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                // Red dot indicator
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .align(Alignment.TopEnd)
                        .offset(x = (-9).dp, y = 9.dp)
                        .clip(CircleShape)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        .background(MaterialTheme.extendedColors.functional.error),
                )
            }

            // User avatar
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(listOf(Rose200, Rose300)),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "头像",
                    modifier = Modifier.size(MaterialTheme.dimens.iconMedium),
                    tint = Color.White,
                )
            }
        }
    }
}

// endregion

// region Empty State

@Composable
private fun DashboardEmptyPreview() {
    val spacing = MaterialTheme.spacing

    Column(modifier = Modifier.fillMaxSize()) {
        DashboardHeaderPreview(username = "Lisa")

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Hero illustration area
                Box(
                    modifier = Modifier.size(160.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    // Main gradient circle
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(Mint100, Mint50, MaterialTheme.colorScheme.surfaceVariant),
                                ),
                            ),
                    )
                    // Camera icon
                    Text(text = "\uD83D\uDCF7", fontSize = 48.sp)
                }

                Spacer(modifier = Modifier.height(spacing.xl))

                // Title
                Text(
                    text = "开启你的变美旅程",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp,
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Subtitle
                Text(
                    text = "只需一张素颜自拍，AI 帮你解读肌肤密码\n见证皮肤一天天变好~",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    fontSize = 15.sp,
                    lineHeight = 24.sp,
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 3-step guide — horizontal layout with colored border circles
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    EmptyStepItemPreview(
                        emoji = "\uD83D\uDCF7",
                        label = "拍照",
                        borderColor = Mint200,
                        bgColor = Mint50,
                    )
                    EmptyStepItemPreview(
                        emoji = "\uD83D\uDD2C",
                        label = "AI 分析",
                        borderColor = Lavender200,
                        bgColor = Lavender50,
                    )
                    EmptyStepItemPreview(
                        emoji = "\uD83D\uDCC8",
                        label = "追踪",
                        borderColor = Rose200,
                        bgColor = Rose50,
                    )
                }

                Spacer(modifier = Modifier.height(spacing.xl))

                // CTA button
                Button(
                    onClick = {},
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(MaterialTheme.dimens.buttonHeight),
                    shape = MaterialTheme.shapes.extraLarge,
                ) {
                    Text(text = "拍第一张自拍", fontSize = 16.sp)
                }

                Spacer(modifier = Modifier.height(spacing.md))

                // Social proof
                Text(
                    text = buildAnnotatedString {
                        append("已有 ")
                        withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                            ),
                        ) {
                            append("12,580")
                        }
                        append(" 位用户在使用")
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                )
            }
        }
    }
}

@Composable
private fun EmptyStepItemPreview(
    emoji: String,
    label: String,
    borderColor: Color,
    bgColor: Color,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(bgColor)
                .border(1.5.dp, borderColor, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = emoji, fontSize = 16.sp)
        }
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

// endregion

// region Content State

@Composable
private fun DashboardContentPreview() {
    val spacing = MaterialTheme.spacing

    Column(modifier = Modifier.fillMaxSize()) {
        DashboardHeaderPreview(username = "Lisa")

        LazyColumn(
            contentPadding = PaddingValues(horizontal = spacing.md, vertical = spacing.sm),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // 1. Hero Card
            item(key = "hero") {
                HeroCardPreview()
            }

            // 2. Mini Metric Row
            item(key = "metrics") {
                MiniMetricRowPreview()
            }

            // 3. Camera Reminder Card
            item(key = "camera_reminder") {
                CameraReminderCardPreview()
            }

            // 4. Quick Actions Grid
            item(key = "quick_actions") {
                QuickActionsGridPreview()
            }

            // 5. Skincare Tip Card
            item(key = "tip") {
                SkincareTipCardPreview()
            }

            // 6. Check-in Card
            item(key = "streak") {
                CheckInCardPreview(streak = 7)
            }

            // 7. Trend Chart Card
            item(key = "trend") {
                TrendChartCardPreview()
            }

            // Bottom spacing
            item { Spacer(modifier = Modifier.height(spacing.md)) }
        }
    }
}

// endregion

// region Hero Card

@Composable
private fun HeroCardPreview() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush = MaterialTheme.gradients.hero),
        ) {
            // Decorative circles
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .offset(x = 100.dp, y = (-50).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f)),
            )
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .offset(x = (-30).dp, y = 80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.05f)),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 18.dp),
            ) {
                // Top: ScoreRing + status text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ScoreRing(
                        score = 82,
                        label = "总评分",
                        size = 84.dp,
                        strokeWidth = 6.5.dp,
                        scoreColor = Color.White,
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Text(
                            text = "皮肤状态不错哦",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = (-0.3).sp,
                        )
                        Text(
                            text = "各项指标都在稳步改善中，继续保持~",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.72f),
                            lineHeight = 18.sp,
                        )
                    }
                }

                // Bottom: trend pill + date
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Trend pill
                    Box(
                        modifier = Modifier
                            .clip(FullRoundedShape)
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 14.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = "\u2191 较上周 +3.2",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                        )
                    }

                    Text(
                        text = "2026.03.14 记录",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.55f),
                    )
                }
            }
        }
    }
}

// endregion

// region Mini Metric Row

@Composable
private fun MiniMetricRowPreview() {
    val skinMetric = MaterialTheme.extendedColors.skinMetric

    data class MetricItem(
        val label: String,
        val score: Int,
        val color: Color,
    )

    val metrics = listOf(
        MetricItem("痘痘", 85, skinMetric.acne),
        MetricItem("毛孔", 78, skinMetric.pore),
        MetricItem("均匀", 82, skinMetric.evenness),
        MetricItem("泛红", 75, skinMetric.redness),
        MetricItem("水润", 80, skinMetric.hydration),
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        metrics.forEach { metric ->
            MiniMetricCardPreview(
                label = metric.label,
                score = metric.score,
                color = metric.color,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun MiniMetricCardPreview(
    label: String,
    score: Int,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRect(
                        color = color,
                        topLeft = Offset.Zero,
                        size = Size(size.width, 2.dp.toPx()),
                    )
                }
                .padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Score value (colored)
            Text(
                text = score.toString(),
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color,
                lineHeight = 16.sp,
            )

            // Label
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )

            // Mini bar
            LinearProgressIndicator(
                progress = { score / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .height(3.dp)
                    .clip(FullRoundedShape),
                color = color,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}

// endregion

// region Camera Reminder Card

@Composable
private fun CameraReminderCardPreview() {
    val spacing = MaterialTheme.spacing

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(listOf(Rose50, Color(0xFFFFF5F6))),
                )
                .padding(horizontal = spacing.md, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Camera icon box
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(
                        brush = Brush.linearGradient(listOf(Rose100, Rose50)),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "\uD83D\uDCF7", fontSize = 22.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "今天还没拍照哦，别忘了记录~",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "每天一拍，见证蜕变的美好",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }

            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = Rose400),
                shape = FullRoundedShape,
            ) {
                Text(text = "去拍照", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// endregion

// region Quick Actions Grid

@Composable
private fun QuickActionsGridPreview() {
    val spacing = MaterialTheme.spacing

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            QuickActionCardPreview(
                title = "肌肤趋势",
                subtitle = "7 天变化",
                iconEmoji = "\uD83D\uDCC8",
                iconBgColor = Mint100,
                modifier = Modifier.weight(1f),
            )
            QuickActionCardPreview(
                title = "护肤品",
                subtitle = "6 个在用",
                iconEmoji = "\uD83E\uDDF4",
                iconBgColor = Apricot100,
                modifier = Modifier.weight(1f),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            QuickActionCardPreview(
                title = "归因分析",
                subtitle = "AI 洞察",
                iconEmoji = "\uD83D\uDD2C",
                iconBgColor = Lavender50,
                modifier = Modifier.weight(1f),
            )
            QuickActionCardPreview(
                title = "分享对比",
                subtitle = "记录蜕变",
                iconEmoji = "\uD83D\uDCE4",
                iconBgColor = Rose50,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickActionCardPreview(
    title: String,
    subtitle: String,
    iconEmoji: String,
    iconBgColor: Color,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        onClick = {},
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.md, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Colored icon with rounded square
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBgColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = iconEmoji, fontSize = 20.sp)
            }

            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 1.dp),
                )
            }
        }
    }
}

// endregion

// region Skincare Tip Card

@Composable
private fun SkincareTipCardPreview() {
    val spacing = MaterialTheme.spacing

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(listOf(Lavender50, Rose50)),
                )
                .padding(horizontal = spacing.md, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            // Icon box
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(listOf(Lavender100, Lavender50)),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(text = "\u2600\uFE0F", fontSize = 20.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "今日紫外线偏强",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "记得涂防晒哦~ 出门前 15 分钟使用效果最佳",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
    }
}

// endregion

// region Check-in Card

@Composable
private fun CheckInCardPreview(streak: Int) {
    val spacing = MaterialTheme.spacing
    val weekDays = listOf("一", "二", "三", "四", "五", "六", "日")

    SectionCard {
        // Title row: "每日打卡" left, "🔥连续 N 天" right
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "每日打卡",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Text(text = "\uD83D\uDD25", fontSize = 16.sp)
                Text(
                    text = "连续 $streak 天",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Rose400,
                )
            }
        }

        // Weekly calendar grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            weekDays.forEachIndexed { index, label ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    val isCompleted = index < streak
                    val isToday = index == streak
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .then(
                                when {
                                    isCompleted -> Modifier
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primary)
                                    isToday -> Modifier
                                        .clip(CircleShape)
                                        .background(Mint50)
                                        .border(2.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                    else -> Modifier
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                },
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (isCompleted) {
                            Text(
                                text = "\u2713",
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                            )
                        } else {
                            Text(
                                text = "${10 + index}",
                                fontSize = 12.sp,
                                color = if (isToday) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Bold,
                            )
                        }
                    }
                }
            }
        }

        // Milestone badge
        if (streak >= 7) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp)
                    .clip(MaterialTheme.shapes.small)
                    .background(
                        brush = Brush.linearGradient(listOf(Mint50, Apricot50)),
                    )
                    .padding(horizontal = 12.dp, vertical = spacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "\u2B50 太棒了! 连续打卡 $streak 天，你的坚持正在改变肌肤~",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

// endregion

// region Trend Chart Card

@Composable
private fun TrendChartCardPreview() {
    val spacing = MaterialTheme.spacing
    val periods = listOf(7 to "7 天", 30 to "30 天", 90 to "90 天")
    val selectedPeriod = 7

    SectionCard {
        SectionHeader(
            title = "肌肤趋势",
            trailing = {
                Text(
                    text = "查看全部",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            },
        )

        Spacer(modifier = Modifier.height(spacing.sm))

        // Period pill selector (Box pills matching real code)
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            periods.forEach { (days, label) ->
                val isSelected = selectedPeriod == days
                Box(
                    modifier = Modifier
                        .clip(FullRoundedShape)
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant,
                        )
                        .padding(horizontal = 14.dp, vertical = 5.dp),
                ) {
                    Text(
                        text = label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.sm))

        // Placeholder chart area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(MaterialTheme.shapes.small)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "\uD83D\uDCC8 趋势图表",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

// endregion
