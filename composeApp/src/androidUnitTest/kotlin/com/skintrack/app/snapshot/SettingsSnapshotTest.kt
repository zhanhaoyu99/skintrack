package com.skintrack.app.snapshot

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.skintrack.app.ui.component.MenuItem
import com.skintrack.app.ui.component.SectionCard
import com.skintrack.app.ui.theme.Mint100
import com.skintrack.app.ui.theme.Rose400
import com.skintrack.app.ui.theme.Rose50
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.gradients
import com.skintrack.app.ui.theme.spacing
import org.junit.Test

class SettingsSnapshotTest : SnapshotTestBase() {

    @Test
    fun settings_light() = captureLight {
        SettingsPreview()
    }

    @Test
    fun settings_dark() = captureDark {
        SettingsPreview()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsPreview() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("\u8BBE\u7F6E") },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "\u8FD4\u56DE",
                        )
                    }
                },
            )
        },
    ) { padding ->
        val menuPadding = PaddingValues(horizontal = MaterialTheme.spacing.md, vertical = 2.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = MaterialTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
        ) {
            // Account section
            SettingsSectionTitle(
                title = "\u8D26\u6237",
                modifier = Modifier.padding(top = MaterialTheme.spacing.xs),
            )
            SectionCard(contentPadding = menuPadding) {
                MenuItem(
                    title = "\u7F16\u8F91\u8D44\u6599",
                    subtitle = "\u5934\u50CF\u3001\u6635\u79F0\u3001\u80A4\u8D28",
                    leading = { SettingsMenuIcon(Icons.Default.Person, Mint100, MaterialTheme.colorScheme.primary) },
                    onClick = {},
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                )
                MenuItem(
                    title = "\u4FEE\u6539\u5BC6\u7801",
                    subtitle = "\u4E0A\u6B21\u4FEE\u6539\uFF1A30 \u5929\u524D",
                    leading = { SettingsMenuIcon(Icons.Default.Lock, Color(0xFFEDE9FE), Color(0xFF7C3AED)) },
                    onClick = {},
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                )
                MenuItem(
                    title = "\u4F1A\u5458\u7BA1\u7406",
                    subtitle = "Pro \u4F1A\u5458 \u00B7 2026.12.14 \u5230\u671F",
                    leading = { SettingsMenuIcon(Icons.Default.Star, Color(0xFFFEF3C7), Color(0xFFD97706)) },
                    onClick = {},
                )
            }

            // Notification section
            SettingsSectionTitle(
                title = "\u901A\u77E5",
                modifier = Modifier.padding(top = MaterialTheme.spacing.xs),
            )
            SectionCard(contentPadding = menuPadding) {
                MenuItem(
                    title = "\u6253\u5361\u63D0\u9192",
                    subtitle = "\u6BCF\u5929 20:00",
                    leading = { SettingsMenuIcon(Icons.Default.Notifications, Color(0xFFFEF3C7), Color(0xFFD97706)) },
                    onClick = {},
                    trailing = { Switch(checked = true, onCheckedChange = {}) },
                    showArrow = false,
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                )
                MenuItem(
                    title = "\u5468\u62A5\u901A\u77E5",
                    subtitle = "\u6BCF\u5468\u65E5\u53D1\u9001\u808C\u80A4\u5468\u62A5",
                    leading = { SettingsEmojiMenuIcon("\uD83D\uDCCA", Color(0xFFDBEAFE)) },
                    onClick = {},
                    trailing = { Switch(checked = false, onCheckedChange = {}) },
                    showArrow = false,
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                )
                MenuItem(
                    title = "AI \u5206\u6790\u5B8C\u6210\u901A\u77E5",
                    subtitle = "\u5206\u6790\u5B8C\u6210\u540E\u63A8\u9001\u63D0\u9192",
                    leading = { SettingsEmojiMenuIcon("\uD83D\uDCAC", Rose50) },
                    onClick = {},
                    trailing = { Switch(checked = true, onCheckedChange = {}) },
                    showArrow = false,
                )
            }

            // Data & Privacy section
            SettingsSectionTitle(
                title = "\u6570\u636E\u4E0E\u9690\u79C1",
                modifier = Modifier.padding(top = MaterialTheme.spacing.xs),
            )
            SectionCard(contentPadding = menuPadding) {
                MenuItem(
                    title = "\u6570\u636E\u540C\u6B65",
                    subtitle = "\u6700\u540E\u540C\u6B65\uFF1A\u4ECA\u5929 09:30",
                    leading = { SettingsMenuIcon(Icons.Default.Refresh, Mint100, MaterialTheme.colorScheme.primary) },
                    onClick = {},
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                )
                MenuItem(
                    title = "\u5BFC\u51FA\u6570\u636E",
                    subtitle = "\u5BFC\u51FA\u4E3A JSON / CSV",
                    leading = { SettingsEmojiMenuIcon("\uD83D\uDCE5", MaterialTheme.colorScheme.surfaceVariant) },
                    onClick = {},
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                )
                MenuItem(
                    title = "\u6E05\u9664\u7F13\u5B58",
                    subtitle = "\u5F53\u524D\u7F13\u5B58 23.4 MB",
                    leading = { SettingsMenuIcon(Icons.Default.Delete, Color(0xFFFEF2F2), MaterialTheme.colorScheme.error) },
                    onClick = {},
                )
            }

            // About section
            SettingsSectionTitle(
                title = "\u5173\u4E8E",
                modifier = Modifier.padding(top = MaterialTheme.spacing.xs),
            )
            SectionCard(contentPadding = menuPadding) {
                MenuItem(
                    title = "\u7248\u672C\u4FE1\u606F",
                    subtitle = "v1.0.0 (Build 42)",
                    leading = { SettingsMenuIcon(Icons.Default.Info, MaterialTheme.colorScheme.surfaceVariant) },
                    onClick = {},
                    showArrow = false,
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                )
                MenuItem(
                    title = "\u9690\u79C1\u653F\u7B56",
                    leading = { SettingsEmojiMenuIcon("\uD83D\uDEE1\uFE0F", MaterialTheme.colorScheme.surfaceVariant) },
                    onClick = {},
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                )
                MenuItem(
                    title = "\u7528\u6237\u534F\u8BAE",
                    leading = { SettingsEmojiMenuIcon("\uD83D\uDCC4", MaterialTheme.colorScheme.surfaceVariant) },
                    onClick = {},
                )
            }

            // Bottom actions
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))

            OutlinedButton(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.dimens.buttonHeight),
                shape = RoundedCornerShape(50),
                border = BorderStroke(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                ),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "\uD83D\uDEAA",
                        fontSize = 16.sp,
                    )
                    Text(
                        text = "\u9000\u51FA\u767B\u5F55",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "\u6CE8\u9500\u8D26\u6237",
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable {}
                        .padding(MaterialTheme.spacing.sm),
                )
            }

            // Footer
            SettingsFooter()

            Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
        }
    }
}

@Composable
private fun SettingsSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.primary),
        )
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.3.sp,
        )
    }
}

@Composable
private fun SettingsMenuIcon(
    icon: ImageVector,
    backgroundColor: Color,
    tintColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
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
private fun SettingsEmojiMenuIcon(
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
private fun SettingsFooter() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = MaterialTheme.spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.xs),
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(brush = MaterialTheme.gradients.primary),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "\uD83C\uDF3F", fontSize = 16.sp)
        }
        Text(
            text = "SKINTRACK",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.6.sp,
        )
        Text(
            text = "v1.0.0 (Build 42)",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            fontSize = 11.sp,
        )
        Text(
            text = "\u00A9 2026 SkinTrack. All rights reserved.",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            fontSize = 10.sp,
        )
    }
}
