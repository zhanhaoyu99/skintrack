package com.skintrack.app.snapshot

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skintrack.app.ui.theme.Apricot300
import com.skintrack.app.ui.theme.Apricot400
import com.skintrack.app.ui.theme.FullRoundedShape
import com.skintrack.app.ui.theme.Lavender50
import com.skintrack.app.ui.theme.Mint50
import com.skintrack.app.ui.theme.Rose50
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.spacing
import org.junit.Test

class PaywallScreenSnapshotTest : SnapshotTestBase() {

    @Test
    fun paywallScreen_yearlySelected_light() = captureLight {
        PaywallPreview(yearlySelected = true)
    }

    @Test
    fun paywallScreen_monthlySelected_light() = captureLight {
        PaywallPreview(yearlySelected = false)
    }

    @Test
    fun paywallScreen_yearlySelected_dark() = captureDark {
        PaywallPreview(yearlySelected = true)
    }
}

@Composable
private fun PaywallPreview(yearlySelected: Boolean) {
    val spacing = MaterialTheme.spacing

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.lg, vertical = spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        // Hero section with rose→lavender gradient + crown + title + trial pill + social proof
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                    .background(
                        Brush.verticalGradient(
                            listOf(Rose50, Lavender50, Color.Transparent),
                        ),
                    )
                    .padding(start = spacing.xl, end = spacing.xl, top = spacing.md, bottom = spacing.lg),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    // Crown: 80dp golden gradient circle with glow
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .drawBehind {
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFFFAA00).copy(alpha = 0.15f),
                                            Color.Transparent,
                                        ),
                                    ),
                                    radius = 60.dp.toPx(),
                                )
                            }
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(
                                        Color(0xFFFFD700),
                                        Color(0xFFFFAA00),
                                        Color(0xFFFF8C00),
                                    ),
                                ),
                                shape = CircleShape,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "\uD83D\uDC51",
                            fontSize = 36.sp,
                        )
                    }

                    Text(
                        text = "开启你的\n变美之旅",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center,
                    )
                    Text(
                        text = "科学护肤，让每一天的努力都被看见",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )

                    // Trial badge (green pill)
                    Text(
                        text = "\u24D8 新用户享 14 天免费试用",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF059669),
                        modifier = Modifier
                            .background(
                                brush = Brush.linearGradient(
                                    listOf(Color(0xFFECFDF5), Color(0xFFD1FAE5)),
                                ),
                                shape = FullRoundedShape,
                            )
                            .padding(horizontal = spacing.md, vertical = spacing.sm),
                    )

                    // Social proof: overlapping avatars + subscriber count
                    SocialProofPreview()
                }
            }
        }

        // Benefits list with subtitles (5 items)
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.xs),
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                BenefitItemPreview("无限拍照记录", "不受次数限制，每天追踪肌肤变化")
                BenefitItemPreview("AI 详细分析报告", "痘痘、毛孔、均匀度多维度深度解读")
                BenefitItemPreview("归因分析", "找出最有效的护肤品组合")
                BenefitItemPreview("分享对比卡片", "记录蜕变过程，见证美丽")
                BenefitItemPreview("云端同步", "数据安全存储，永不丢失")
            }
        }

        // Plan selection cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                PlanCardPreview(
                    title = "月度会员",
                    price = "¥19.9",
                    unit = "/月",
                    isSelected = !yearlySelected,
                    modifier = Modifier.weight(1f),
                )
                PlanCardPreview(
                    title = "年度会员",
                    price = "¥168",
                    unit = "/年",
                    isSelected = yearlySelected,
                    originalPrice = "¥238.8/年",
                    perMonth = "约 ¥14/月",
                    savingText = "省 ¥70.8",
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // Trust signals
        item {
            TrustSignalsPreview()
        }

        // Purchase button
        item {
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.dimens.buttonHeight),
                shape = FullRoundedShape,
            ) {
                Text(
                    text = "立即订阅",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // Restore purchase
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                TextButton(onClick = {}) {
                    Text(
                        "恢复购买",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        // Terms notice
        item {
            Text(
                text = "订阅将自动续费，可随时取消。\n订阅即表示同意服务条款和隐私政策。",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun BenefitItemPreview(title: String, subtitle: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 11.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Green check circle (26dp)
        Box(
            modifier = Modifier
                .size(26.dp)
                .background(
                    brush = Brush.linearGradient(
                        listOf(Mint50, MaterialTheme.colorScheme.primaryContainer),
                    ),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "\u2713",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary,
            )
        }
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PlanCardPreview(
    title: String,
    price: String,
    unit: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    originalPrice: String? = null,
    perMonth: String? = null,
    savingText: String? = null,
) {
    val spacing = MaterialTheme.spacing
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outlineVariant
    val containerColor = if (isSelected) Mint50 else MaterialTheme.colorScheme.surface

    Box(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = if (savingText != null) 12.dp else 0.dp)
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = MaterialTheme.shapes.large,
                ),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            shape = MaterialTheme.shapes.large,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = price,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = unit,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp),
                    )
                }
                if (originalPrice != null) {
                    Text(
                        text = originalPrice,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textDecoration = TextDecoration.LineThrough,
                    )
                }
                if (perMonth != null) {
                    Text(
                        text = perMonth,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }

        // Saving badge floating on top
        if (savingText != null) {
            Box(modifier = Modifier.align(Alignment.TopCenter)) {
                Text(
                    text = savingText,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(Apricot300, Apricot400),
                            ),
                            shape = FullRoundedShape,
                        )
                        .padding(horizontal = spacing.md, vertical = spacing.xs),
                )
            }
        }
    }
}

@Composable
private fun SocialProofPreview() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
    ) {
        // 4 overlapping avatar circles (28dp)
        Row(horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
            val avatarData = listOf(
                "L" to listOf(Color(0xFFFDA4AF), Color(0xFFFB7185)),
                "M" to listOf(Color(0xFF58CAA5), Color(0xFF2D9F7F)),
                "S" to listOf(Color(0xFFC4B5FD), Color(0xFFA78BFA)),
                "J" to listOf(Color(0xFFF4A261), Color(0xFFE68A3E)),
            )
            avatarData.forEach { (initial, colors) ->
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .border(2.dp, Color.White, CircleShape)
                        .background(
                            brush = Brush.linearGradient(colors),
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = initial,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 10.sp,
                    )
                }
            }
        }
        Row {
            Text(
                text = "12,580",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = " 位用户已订阅",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun TrustSignalsPreview() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        listOf(
            "\uD83D\uDD12 安全支付",
            "\u2713 随时取消",
            "\u26A1 即时生效",
        ).forEach { tag ->
            Text(
                text = tag,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
