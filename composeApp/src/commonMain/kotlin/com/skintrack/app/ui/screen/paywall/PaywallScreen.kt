package com.skintrack.app.ui.screen.paywall

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.isSystemInDarkTheme
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.skintrack.app.domain.model.SubscriptionPlan
import com.skintrack.app.ui.theme.Apricot300
import com.skintrack.app.ui.theme.Apricot400
import com.skintrack.app.ui.theme.FullRoundedShape
import com.skintrack.app.ui.theme.Lavender50
import com.skintrack.app.ui.theme.Mint50
import com.skintrack.app.ui.theme.Rose50
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.spacing
import org.koin.compose.viewmodel.koinViewModel

class PaywallScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: PaywallViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(uiState.success) {
            if (uiState.success) navigator.pop()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.Default.Close, contentDescription = "关闭")
                        }
                    },
                )
            },
        ) { padding ->
            PaywallContent(
                uiState = uiState,
                onSelectPlan = viewModel::selectPlan,
                onPurchase = viewModel::purchase,
                onRestore = viewModel::restorePurchase,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun PaywallContent(
    uiState: PaywallUiState,
    onSelectPlan: (SubscriptionPlan) -> Unit,
    onPurchase: () -> Unit,
    onRestore: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = spacing.lg, vertical = spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        // Brand hero section with rose→lavender gradient
        item(key = "hero") {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                if (isSystemInDarkTheme()) Rose50.copy(alpha = 0.12f) else Rose50,
                                if (isSystemInDarkTheme()) Lavender50.copy(alpha = 0.08f) else Lavender50,
                                Color.Transparent,
                            ),
                        ),
                    )
                    .padding(start = spacing.xl, end = spacing.xl, top = spacing.md, bottom = spacing.lg),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacing.sm),
                ) {
                    // Crown: 80dp golden gradient circle with breathing glow
                    val crownGlow = rememberInfiniteTransition()
                    val glowAlpha by crownGlow.animateFloat(
                        initialValue = 0.35f,
                        targetValue = 0.5f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 3000),
                            repeatMode = RepeatMode.Reverse,
                        ),
                    )
                    val glowRadius by crownGlow.animateFloat(
                        initialValue = 60f,
                        targetValue = 80f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 3000),
                            repeatMode = RepeatMode.Reverse,
                        ),
                    )
                    val outerGlowAlpha by crownGlow.animateFloat(
                        initialValue = 0.1f,
                        targetValue = 0.18f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(durationMillis = 3000),
                            repeatMode = RepeatMode.Reverse,
                        ),
                    )
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .drawBehind {
                                // Outer soft glow
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFFFAA00).copy(alpha = outerGlowAlpha),
                                            Color.Transparent,
                                        ),
                                    ),
                                    radius = glowRadius.dp.toPx(),
                                )
                                // Inner glow
                                drawCircle(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFFFFAA00).copy(alpha = glowAlpha),
                                            Color.Transparent,
                                        ),
                                    ),
                                    radius = 48.dp.toPx(),
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

                    // Trial badge (green pill with gradient)
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
                    SocialProofInline()
                }
            }
        }

        // Benefits list with subtitles
        item(key = "benefits") {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = spacing.xs),
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                BenefitItem("无限拍照记录", "不受次数限制，每天追踪肌肤变化")
                BenefitItem("AI 详细分析报告", "痘痘、毛孔、均匀度多维度深度解读")
                BenefitItem("归因分析", "找出最有效的护肤品组合")
                BenefitItem("分享对比卡片", "记录蜕变过程，见证美丽")
                BenefitItem("云端同步", "数据安全存储，永不丢失")
            }
        }

        // Plan selection
        item(key = "plans") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                PlanCard(
                    title = "月度会员",
                    price = "¥19.9",
                    unit = "/月",
                    isSelected = uiState.selectedPlan == SubscriptionPlan.MONTHLY,
                    onClick = { onSelectPlan(SubscriptionPlan.MONTHLY) },
                    modifier = Modifier.weight(1f),
                )
                PlanCard(
                    title = "年度会员",
                    price = "¥168",
                    unit = "/年",
                    originalPrice = "¥238.8/年",
                    perMonth = "约 ¥14/月",
                    savingText = "省 ¥70.8",
                    isSelected = uiState.selectedPlan == SubscriptionPlan.YEARLY,
                    onClick = { onSelectPlan(SubscriptionPlan.YEARLY) },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        // Trust signals
        item(key = "trust") {
            TrustSignals()
        }

        // Purchase button
        item(key = "purchase") {
            Button(
                onClick = onPurchase,
                enabled = !uiState.isPurchasing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.dimens.buttonHeight),
                shape = FullRoundedShape,
            ) {
                Text(
                    text = if (uiState.isPurchasing) "处理中..." else "立即订阅",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // Error message
        if (uiState.error != null) {
            item(key = "error") {
                Text(
                    text = uiState.error,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        // Restore purchase
        item(key = "restore") {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                TextButton(onClick = onRestore) {
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
        item(key = "terms") {
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
private fun BenefitItem(title: String, subtitle: String) {
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
                    brush = Brush.linearGradient(listOf(Mint50, MaterialTheme.colorScheme.primaryContainer)),
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
private fun PlanCard(
    title: String,
    price: String,
    unit: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    originalPrice: String? = null,
    perMonth: String? = null,
    savingText: String? = null,
) {
    val spacing = MaterialTheme.spacing
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outlineVariant
    val containerColor = if (isSelected) {
        Mint50
    } else {
        MaterialTheme.colorScheme.surface
    }

    Box(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = if (savingText != null) 12.dp else 0.dp)
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = MaterialTheme.shapes.large,
                )
                .clickable(onClick = onClick),
            colors = CardDefaults.cardColors(containerColor = containerColor),
            shape = MaterialTheme.shapes.large,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isSelected) {
                            Modifier.drawBehind {
                                // Top shine line: transparent → primary → transparent
                                val shineBrush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color(0xFF2D9F7F).copy(alpha = 0.4f),
                                        Color.Transparent,
                                    ),
                                )
                                drawRect(
                                    brush = shineBrush,
                                    size = Size(size.width, 2.dp.toPx()),
                                )
                            }
                        } else {
                            Modifier
                        },
                    )
                    .padding(horizontal = 12.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(spacing.xs),
            ) {
                // Plan period label
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                // Price: large number + small unit
                Row(
                    verticalAlignment = Alignment.Bottom,
                ) {
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
            Box(
                modifier = Modifier.align(Alignment.TopCenter),
            ) {
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
private fun SocialProofInline() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
    ) {
        // 4 overlapping avatar circles (28dp)
        Row(
            horizontalArrangement = Arrangement.spacedBy((-8).dp),
        ) {
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
                        .shadow(2.dp, CircleShape)
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
private fun TrustSignals() {
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
