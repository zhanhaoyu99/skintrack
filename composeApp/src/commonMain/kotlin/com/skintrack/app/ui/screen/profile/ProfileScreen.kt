package com.skintrack.app.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.skintrack.app.domain.model.AuthUser
import com.skintrack.app.ui.component.LoadingContent
import com.skintrack.app.ui.component.MenuItem
import com.skintrack.app.ui.component.ScoreRing
import androidx.compose.foundation.layout.PaddingValues
import com.skintrack.app.ui.component.SectionCard
import com.skintrack.app.ui.component.animateListItem
import com.skintrack.app.ui.screen.attribution.AttributionReportScreen
import com.skintrack.app.ui.screen.auth.AuthScreen
import com.skintrack.app.ui.screen.paywall.PaywallScreen
import com.skintrack.app.ui.screen.product.ProductManageScreen
import com.skintrack.app.ui.screen.settings.EditProfileScreen
import com.skintrack.app.ui.screen.settings.SettingsScreen
import com.skintrack.app.ui.theme.Apricot400
import com.skintrack.app.ui.theme.Apricot50
import com.skintrack.app.ui.theme.Lavender50
import com.skintrack.app.ui.theme.Lavender400
import com.skintrack.app.ui.theme.Mint50
import com.skintrack.app.ui.theme.Rose200
import com.skintrack.app.ui.theme.Rose300
import com.skintrack.app.ui.theme.Rose400
import com.skintrack.app.ui.theme.Rose50
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.gradients
import com.skintrack.app.ui.theme.spacing
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val authUser by viewModel.authUser.collectAsState()
    val navigator = LocalNavigator.currentOrThrow

    Column(modifier = Modifier.fillMaxSize()) {
        when (val state = uiState) {
            ProfileUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    LoadingContent()
                }
            }

            is ProfileUiState.Content -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                ) {
                    // Gradient Header
                    ProfileHeader(
                        authUser = authUser,
                        skinType = state.skinType,
                        onSettingsClick = { navigator.push(SettingsScreen()) },
                        onProfileClick = { navigator.push(EditProfileScreen()) },
                    )

                    // Floating stats card (overlaps header)
                    StatsCard(
                        state = state,
                        modifier = Modifier
                            .padding(horizontal = MaterialTheme.spacing.lg)
                            .offset(y = (-36).dp)
                            .animateListItem(0),
                    )

                    // Content below stats card
                    Column(
                        modifier = Modifier
                            .padding(horizontal = MaterialTheme.spacing.lg)
                            .offset(y = (-20).dp),
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
                    ) {
                        // Skin Goals
                        SkinGoalsSection(
                            goals = state.skinGoals,
                            modifier = Modifier.animateListItem(1),
                        )

                        // Menu
                        MenuSection(
                            modifier = Modifier.animateListItem(2),
                            onProductManage = { navigator.push(ProductManageScreen()) },
                            onAttributionReport = { navigator.push(AttributionReportScreen()) },
                            onMemberCenter = { navigator.push(PaywallScreen()) },
                            onLogout = {
                                viewModel.logout()
                                navigator.replaceAll(AuthScreen())
                            },
                        )

                        // App Footer
                        AppFooter(modifier = Modifier.animateListItem(3))
                    }
                }
            }
        }
    }
}

// region Header

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfileHeader(
    authUser: AuthUser?,
    skinType: String? = null,
    onSettingsClick: () -> Unit,
    onProfileClick: () -> Unit = {},
) {
    val spacing = MaterialTheme.spacing

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                // Decorative semi-transparent circles
                drawCircle(
                    color = Color.White.copy(alpha = 0.06f),
                    radius = 110.dp.toPx(),
                    center = Offset(size.width * 0.85f, size.height * 0.2f),
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.04f),
                    radius = 80.dp.toPx(),
                    center = Offset(size.width * 0.1f, size.height * 0.7f),
                )
            }
            .background(brush = MaterialTheme.gradients.hero)
            .padding(bottom = 52.dp), // Extra bottom padding for floating card overlap
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = spacing.lg),
        ) {
            // Title bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.xl, bottom = spacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "我的",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "设置",
                        tint = Color.White,
                    )
                }
            }

            // User info (clickable → EditProfile)
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = spacing.md)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable(onClick = onProfileClick),
            ) {
                // Avatar with Rose gradient + white border
                Box(
                    modifier = Modifier
                        .size(MaterialTheme.dimens.avatarExtraLarge)
                        .border(3.dp, Color.White.copy(alpha = 0.35f), CircleShape)
                        .padding(3.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(listOf(Rose200, Rose300)),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    val initial = authUser?.displayName?.firstOrNull()
                        ?: authUser?.email?.firstOrNull()
                        ?: '用'
                    Text(
                        text = initial.uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    // Username + VIP badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        Text(
                            text = authUser?.displayName ?: authUser?.email ?: "本地用户",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 22.sp,
                        )
                        // VIP badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(brush = MaterialTheme.gradients.vipBadge)
                                .padding(horizontal = 10.dp, vertical = 2.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                Text(
                                    text = "\u2B50",
                                    fontSize = 10.sp,
                                )
                                Text(
                                    text = "VIP",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF7B4C00),
                                    fontSize = 11.sp,
                                    letterSpacing = 0.3.sp,
                                )
                            }
                        }
                    }

                    // Email
                    Text(
                        text = if (authUser != null) authUser.email else "登录后同步数据",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 13.sp,
                    )

                    // Tag pills
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                        verticalArrangement = Arrangement.spacedBy(spacing.xs),
                    ) {
                        ProfilePill("Pro 会员")
                        val skinLabel = when (skinType?.uppercase()) {
                            "OILY" -> "油性肌"
                            "DRY" -> "干性肌"
                            "COMBINATION" -> "混合肌"
                            "SENSITIVE" -> "敏感肌"
                            "NORMAL" -> "中性肌"
                            else -> null
                        }
                        if (skinLabel != null) {
                            ProfilePill(skinLabel)
                        }
                    }
                }

                // Chevron right arrow
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Composable
private fun ProfilePill(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.18f))
            .padding(horizontal = 12.dp, vertical = 3.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color.White,
            fontSize = 12.sp,
        )
    }
}

// endregion

// region Stats Card

@Composable
private fun StatsCard(state: ProfileUiState.Content, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.92f),
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(horizontal = 12.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatItem(
                label = "总记录",
                value = "${state.totalRecords}",
                iconBg = Mint50,
                iconEmoji = "\uD83D\uDCCB",
                valueColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            VerticalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = MaterialTheme.spacing.sm),
            )
            StatItem(
                label = "连续打卡",
                value = "${state.currentStreak}",
                iconBg = Apricot50,
                iconEmoji = "\u26A1",
                valueColor = Apricot400,
                modifier = Modifier.weight(1f),
            )
            VerticalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = MaterialTheme.spacing.sm),
            )
            StatItem(
                label = "护肤品",
                value = "${state.totalProducts}",
                iconBg = Lavender50,
                iconEmoji = "\uD83E\uDDF4",
                modifier = Modifier.weight(1f),
            )
            VerticalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = MaterialTheme.spacing.sm),
            )
            // Latest score with mini ScoreRing
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                if (state.latestScore != null) {
                    ScoreRing(
                        score = state.latestScore,
                        size = 34.dp,
                        strokeWidth = 3.dp,
                        label = "",
                    )
                } else {
                    Text(
                        text = "--",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 24.sp,
                    )
                }
                Text(
                    text = "最新评分",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    iconBg: Color,
    iconEmoji: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Color.Unspecified,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(MaterialTheme.dimens.menuIconSize)
                .clip(RoundedCornerShape(10.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = iconEmoji, fontSize = 14.sp)
        }
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            color = valueColor,
            letterSpacing = (-0.5).sp,
            textAlign = TextAlign.Center,
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )
    }
}

// endregion

// region Skin Goals

private val goalDisplayMap = mapOf(
    "acne" to Triple("祛痘", Rose50, Rose400),
    "pore" to Triple("收毛孔", Mint50, null), // null = use primary
    "brighten" to Triple("提亮", Lavender50, Lavender400),
    "hydrate" to Triple("补水", Color(0xFFE8F4FD), Color(0xFF3B82F6)),
    "anti_aging" to Triple("抗老", Color(0xFFFCE7F3), Color(0xFFBE185D)),
    "redness" to Triple("退红", Color(0xFFFFF1F2), Color(0xFFE11D48)),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SkinGoalsSection(
    goals: List<String>,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    val primary = MaterialTheme.colorScheme.primary

    SectionCard(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = spacing.md, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "我的护肤目标",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            )
            Text(
                text = "编辑",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
            )
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
            modifier = Modifier.padding(top = spacing.sm),
        ) {
            if (goals.isEmpty()) {
                // Show default goals when none set
                GoalPill("祛痘", Rose50, Rose400)
                GoalPill("收毛孔", Mint50, primary)
                GoalPill("提亮", Lavender50, Lavender400)
            } else {
                goals.forEach { goalId ->
                    val (label, bg, textColor) = goalDisplayMap[goalId]
                        ?: Triple(goalId, Mint50, null)
                    GoalPill(label, bg, textColor ?: primary)
                }
            }
            // "+ 添加" pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            ) {
                Text(
                    text = "+ 添加",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun GoalPill(
    text: String,
    bgColor: Color,
    textColor: Color,
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .padding(horizontal = 14.dp, vertical = 6.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            fontSize = 13.sp,
        )
    }
}

// endregion

// region Menu Section

@Composable
private fun MenuSection(
    modifier: Modifier = Modifier,
    onProductManage: () -> Unit,
    onAttributionReport: () -> Unit,
    onMemberCenter: () -> Unit,
    onLogout: () -> Unit,
) {
    val spacing = MaterialTheme.spacing

    val menuPadding = PaddingValues(horizontal = spacing.md, vertical = 2.dp)

    // Function group
    SectionCard(modifier = modifier, contentPadding = menuPadding) {
        MenuItem(
            title = "护肤品管理",
            subtitle = "管理你的护肤品清单",
            leading = { EmojiMenuIcon("\uD83E\uDDF4", Apricot50) },
            onClick = onProductManage,
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = spacing.md))
        MenuItem(
            title = "归因分析报告",
            subtitle = "查看护肤品效果分析",
            leading = { EmojiMenuIcon("\uD83D\uDCCA", Color(0xFFEDE9FE)) },
            onClick = onAttributionReport,
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = spacing.md))
        MenuItem(
            title = "会员中心",
            subtitle = "Pro 会员 · 2026.12.14 到期",
            leading = { EmojiMenuIcon("\uD83D\uDC51", Color(0xFFFEF3C7)) },
            onClick = onMemberCenter,
        )
    }

    // System group
    SectionCard(contentPadding = menuPadding) {
        MenuItem(
            title = "打卡提醒",
            subtitle = "每天 20:00 提醒",
            leading = { MenuIcon(Icons.Default.Notifications, Color(0xFFFEF3C7), Color(0xFFD97706)) },
            onClick = {},
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = spacing.md))
        MenuItem(
            title = "数据同步",
            subtitle = "已同步",
            leading = { MenuIcon(Icons.Default.Refresh, Color(0xFFDBEAFE), Color(0xFF3B82F6)) },
            trailing = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Mint50)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "已同步",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 11.sp,
                    )
                }
            },
            onClick = {},
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = spacing.md))
        MenuItem(
            title = "关于 SkinTrack",
            subtitle = "版本 1.0.0",
            leading = { MenuIcon(Icons.Default.Info, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant) },
            onClick = {},
        )
    }

    // Logout
    SectionCard(contentPadding = menuPadding) {
        MenuItem(
            title = "退出登录",
            onClick = onLogout,
            textColor = MaterialTheme.colorScheme.error,
            showArrow = false,
        )
    }
}

@Composable
private fun MenuIcon(
    icon: ImageVector,
    backgroundColor: Color,
    tintColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tintColor,
            modifier = Modifier.size(MaterialTheme.dimens.iconSmall),
        )
    }
}

@Composable
private fun EmojiMenuIcon(
    emoji: String,
    backgroundColor: Color,
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = emoji, fontSize = 16.sp)
    }
}

// endregion

// region App Footer

@Composable
private fun AppFooter(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
    ) {
        // Logo mark
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(brush = MaterialTheme.gradients.primary),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "\uD83C\uDF3F",
                fontSize = 18.sp,
            )
        }
        Text(
            text = "SKINTRACK",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.5.sp,
        )
        Text(
            text = "v1.0.0 (Build 42)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
        )
    }
}

// endregion
