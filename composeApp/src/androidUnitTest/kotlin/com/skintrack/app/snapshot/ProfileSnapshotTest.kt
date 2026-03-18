package com.skintrack.app.snapshot

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
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
import com.skintrack.app.ui.component.MenuItem
import com.skintrack.app.ui.component.ScoreRing
import com.skintrack.app.ui.component.SectionCard
import com.skintrack.app.ui.theme.Apricot400
import com.skintrack.app.ui.theme.Apricot50
import com.skintrack.app.ui.theme.Lavender400
import com.skintrack.app.ui.theme.Lavender50
import com.skintrack.app.ui.theme.Mint50
import com.skintrack.app.ui.theme.Rose200
import com.skintrack.app.ui.theme.Rose300
import com.skintrack.app.ui.theme.Rose400
import com.skintrack.app.ui.theme.Rose50
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.gradients
import com.skintrack.app.ui.theme.spacing
import org.junit.Test

class ProfileSnapshotTest : SnapshotTestBase() {

    @Test
    fun profile_light() = captureLight {
        ProfilePreview()
    }

    @Test
    fun profile_dark() = captureDark {
        ProfilePreview()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfilePreview() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        // Gradient Header
        ProfileHeader()

        // Floating stats card (overlaps header)
        StatsCard(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.spacing.lg)
                .offset(y = (-36).dp),
        )

        // Content below stats card
        Column(
            modifier = Modifier
                .padding(horizontal = MaterialTheme.spacing.lg)
                .offset(y = (-20).dp),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
        ) {
            // Skin Goals
            SkinGoalsSection()

            // Menu sections
            MenuSection()

            // App Footer
            AppFooter()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProfileHeader() {
    val spacing = MaterialTheme.spacing

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
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
            .padding(bottom = 52.dp),
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
                    text = "\u6211\u7684",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "\u8BBE\u7F6E",
                        tint = Color.White,
                    )
                }
            }

            // User info
            Row(
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = spacing.md),
            ) {
                // Avatar
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
                    Text(
                        text = "L",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(spacing.xs),
                ) {
                    // Username + VIP badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        Text(
                            text = "Lisa",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            fontSize = 22.sp,
                        )
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
                                Text(text = "\u2B50", fontSize = 10.sp)
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
                        text = "lisa@example.com",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 13.sp,
                    )

                    // Tag pills
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        ProfilePill("Pro \u4F1A\u5458")
                        ProfilePill("\u6DF7\u5408\u808C")
                    }
                }
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

@Composable
private fun StatsCard(modifier: Modifier = Modifier) {
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
                label = "\u603B\u8BB0\u5F55",
                value = "42",
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
                label = "\u8FDE\u7EED\u6253\u5361",
                value = "7",
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
                label = "\u62A4\u80A4\u54C1",
                value = "8",
                iconBg = Lavender50,
                iconEmoji = "\uD83E\uDDF4",
                modifier = Modifier.weight(1f),
            )
            VerticalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = MaterialTheme.spacing.sm),
            )
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                ScoreRing(
                    score = 82,
                    size = 34.dp,
                    strokeWidth = 3.dp,
                    label = "",
                )
                Text(
                    text = "\u6700\u65B0\u8BC4\u5206",
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SkinGoalsSection() {
    val primary = MaterialTheme.colorScheme.primary

    SectionCard(
        contentPadding = PaddingValues(horizontal = MaterialTheme.spacing.md, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "\u6211\u7684\u62A4\u80A4\u76EE\u6807",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            )
            Text(
                text = "\u7F16\u8F91",
                style = MaterialTheme.typography.bodySmall,
                color = primary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
            )
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            modifier = Modifier.padding(top = MaterialTheme.spacing.sm),
        ) {
            GoalPill("\u7960\u75D8", Rose50, Rose400)
            GoalPill("\u6536\u6BDB\u5B54", Mint50, primary)
            GoalPill("\u63D0\u4EAE", Lavender50, Lavender400)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            ) {
                Text(
                    text = "+ \u6DFB\u52A0",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun GoalPill(text: String, bgColor: Color, textColor: Color) {
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

@Composable
private fun MenuSection() {
    val spacing = MaterialTheme.spacing
    val menuPadding = PaddingValues(horizontal = spacing.md, vertical = 2.dp)

    // Function group
    SectionCard(contentPadding = menuPadding) {
        MenuItem(
            title = "\u62A4\u80A4\u54C1\u7BA1\u7406",
            subtitle = "\u7BA1\u7406\u4F60\u7684\u62A4\u80A4\u54C1\u6E05\u5355",
            leading = { EmojiMenuIcon("\uD83E\uDDF4", Apricot50) },
            onClick = {},
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = spacing.md))
        MenuItem(
            title = "\u5F52\u56E0\u5206\u6790\u62A5\u544A",
            subtitle = "\u67E5\u770B\u62A4\u80A4\u54C1\u6548\u679C\u5206\u6790",
            leading = { EmojiMenuIcon("\uD83D\uDCCA", Color(0xFFEDE9FE)) },
            onClick = {},
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = spacing.md))
        MenuItem(
            title = "\u4F1A\u5458\u4E2D\u5FC3",
            subtitle = "Pro \u4F1A\u5458 \u00B7 2026.12.14 \u5230\u671F",
            leading = { EmojiMenuIcon("\uD83D\uDC51", Color(0xFFFEF3C7)) },
            onClick = {},
        )
    }

    // System group
    SectionCard(contentPadding = menuPadding) {
        MenuItem(
            title = "\u6253\u5361\u63D0\u9192",
            subtitle = "\u6BCF\u5929 20:00 \u63D0\u9192",
            leading = { MenuIcon(Icons.Default.Notifications, Color(0xFFFEF3C7), Color(0xFFD97706)) },
            onClick = {},
        )
        HorizontalDivider(modifier = Modifier.padding(horizontal = spacing.md))
        MenuItem(
            title = "\u6570\u636E\u540C\u6B65",
            subtitle = "\u5DF2\u540C\u6B65",
            leading = { MenuIcon(Icons.Default.Refresh, Color(0xFFDBEAFE), Color(0xFF3B82F6)) },
            trailing = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(Mint50)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "\u5DF2\u540C\u6B65",
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
            title = "\u5173\u4E8E SkinTrack",
            subtitle = "\u7248\u672C 1.0.0",
            leading = {
                MenuIcon(
                    Icons.Default.Info,
                    MaterialTheme.colorScheme.surfaceVariant,
                    MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            onClick = {},
        )
    }

    // Logout
    SectionCard(contentPadding = menuPadding) {
        MenuItem(
            title = "\u9000\u51FA\u767B\u5F55",
            onClick = {},
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

@Composable
private fun AppFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(brush = MaterialTheme.gradients.primary),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "\uD83C\uDF3F", fontSize = 18.sp)
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
