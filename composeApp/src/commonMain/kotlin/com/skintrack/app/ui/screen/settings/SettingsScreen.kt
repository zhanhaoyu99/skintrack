package com.skintrack.app.ui.screen.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh

import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.skintrack.app.ui.component.DeleteAccountDialog
import com.skintrack.app.ui.component.MenuItem
import androidx.compose.foundation.layout.PaddingValues
import com.skintrack.app.ui.component.SectionCard
import com.skintrack.app.ui.component.animateCardEntrance
import com.skintrack.app.ui.screen.auth.AuthScreen
import com.skintrack.app.ui.screen.paywall.PaywallScreen
import com.skintrack.app.ui.theme.Primary100
import com.skintrack.app.ui.theme.Rose50
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.gradients
import com.skintrack.app.ui.theme.spacing
import org.koin.compose.viewmodel.koinViewModel

class SettingsScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel: SettingsViewModel = koinViewModel()
        val navigator = LocalNavigator.currentOrThrow
        val reminderEnabled by viewModel.reminderEnabled.collectAsState()
        val reminderTime by viewModel.reminderTime.collectAsState()
        val weeklyReportEnabled by viewModel.weeklyReportEnabled.collectAsState()
        val aiNotificationEnabled by viewModel.aiNotificationEnabled.collectAsState()
        val exportState by viewModel.exportState.collectAsState()
        val showDeleteDialog by viewModel.showDeleteDialog.collectAsState()
        val deleteState by viewModel.deleteState.collectAsState()
        val showLogoutConfirm by viewModel.showLogoutConfirm.collectAsState()
        val snackbarMessage by viewModel.snackbarMessage.collectAsState()
        val loggedOut by viewModel.loggedOut.collectAsState()

        val snackbarHostState = remember { SnackbarHostState() }

        // Navigate to auth on logout/delete
        LaunchedEffect(loggedOut) {
            if (loggedOut) {
                navigator.replaceAll(AuthScreen())
            }
        }

        // Show snackbar messages
        LaunchedEffect(snackbarMessage) {
            snackbarMessage?.let {
                snackbarHostState.showSnackbar(it)
                viewModel.clearSnackbar()
            }
        }

        // Export data confirmation dialog (loading)
        if (exportState is ExportState.Loading) {
            AlertDialog(
                onDismissRequest = { /* Cannot dismiss while loading */ },
                title = { Text("导出数据") },
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(MaterialTheme.dimens.buttonSpinnerSize),
                            strokeWidth = 2.dp,
                        )
                        Text(
                            text = "正在导出数据...",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                },
                confirmButton = {},
            )
        }

        // Export data success dialog
        if (exportState is ExportState.Success) {
            AlertDialog(
                onDismissRequest = { viewModel.resetExportState() },
                title = { Text("导出完成") },
                text = {
                    Text(
                        text = "数据导出成功，已生成 JSON 格式文件。",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.resetExportState() }) {
                        Text("确定")
                    }
                },
            )
        }

        // Logout confirmation dialog
        if (showLogoutConfirm) {
            AlertDialog(
                onDismissRequest = { viewModel.dismissLogoutConfirm() },
                title = { Text("退出登录") },
                text = {
                    Text(
                        text = "确定要退出登录吗？",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                confirmButton = {
                    TextButton(onClick = { viewModel.confirmLogout() }) {
                        Text("退出", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.dismissLogoutConfirm() }) {
                        Text("取消")
                    }
                },
            )
        }

        // Delete account dialog
        if (showDeleteDialog) {
            DeleteAccountDialog(
                onConfirm = { password -> viewModel.deleteAccount(password) },
                onDismiss = { viewModel.dismissDeleteAccountDialog() },
                isLoading = deleteState is DeleteState.Loading,
            )
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("设置") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "返回",
                            )
                        }
                    },
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { padding ->
            val menuPadding = PaddingValues(horizontal = MaterialTheme.spacing.md, vertical = MaterialTheme.spacing.xs)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = MaterialTheme.spacing.md),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),
            ) {
                // ── Account section ──────────────────────────────────────
                SettingsSectionTitle(
                    title = "账户",
                    modifier = Modifier
                        .padding(top = MaterialTheme.spacing.xs)
                        .animateCardEntrance(0),
                )
                SectionCard(modifier = Modifier.animateCardEntrance(1), contentPadding = menuPadding) {
                    MenuItem(
                        title = "编辑资料",
                        subtitle = "头像、昵称、肤质",
                        leading = { MenuIcon(Icons.Default.Person, Primary100, MaterialTheme.colorScheme.primary) },
                        onClick = { navigator.push(EditProfileScreen()) },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                    )
                    MenuItem(
                        title = "修改密码",
                        subtitle = "上次修改：30 天前",
                        leading = { MenuIcon(Icons.Default.Lock, Color(0xFFEDE9FE), Color(0xFF7C3AED)) },
                        onClick = { navigator.push(ChangePasswordScreen()) },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                    )
                    MenuItem(
                        title = "会员管理",
                        subtitle = "Pro 会员 · 2026.12.14 到期",
                        leading = { MenuIcon(Icons.Default.Star, Color(0xFFFEF3C7), Color(0xFFD97706)) },
                        onClick = { navigator.push(PaywallScreen()) },
                    )
                }

                // ── Notification section ─────────────────────────────────
                SettingsSectionTitle(
                    title = "通知",
                    modifier = Modifier
                        .padding(top = MaterialTheme.spacing.xs)
                        .animateCardEntrance(2),
                )
                SectionCard(modifier = Modifier.animateCardEntrance(3), contentPadding = menuPadding) {
                    MenuItem(
                        title = "打卡提醒",
                        subtitle = "每天 $reminderTime",
                        leading = { MenuIcon(Icons.Default.Notifications, Color(0xFFFEF3C7), Color(0xFFD97706)) },
                        onClick = { viewModel.toggleReminder(!reminderEnabled) },
                        trailing = {
                            Switch(
                                checked = reminderEnabled,
                                onCheckedChange = { viewModel.toggleReminder(it) },
                            )
                        },
                        showArrow = false,
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                    )
                    MenuItem(
                        title = "周报通知",
                        subtitle = "每周日发送肌肤周报",
                        leading = { EmojiMenuIcon("\uD83D\uDCCA", Color(0xFFDBEAFE)) },
                        onClick = { viewModel.toggleWeeklyReport() },
                        trailing = {
                            Switch(
                                checked = weeklyReportEnabled,
                                onCheckedChange = { viewModel.toggleWeeklyReport() },
                            )
                        },
                        showArrow = false,
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                    )
                    MenuItem(
                        title = "AI 分析完成通知",
                        subtitle = "分析完成后推送提醒",
                        leading = { EmojiMenuIcon("\uD83D\uDCAC", Rose50) },
                        onClick = { viewModel.toggleAiNotification() },
                        trailing = {
                            Switch(
                                checked = aiNotificationEnabled,
                                onCheckedChange = { viewModel.toggleAiNotification() },
                            )
                        },
                        showArrow = false,
                    )
                }

                // ── Data & Privacy section ─────────────────────────────────
                SettingsSectionTitle(
                    title = "数据与隐私",
                    modifier = Modifier
                        .padding(top = MaterialTheme.spacing.xs)
                        .animateCardEntrance(4),
                )
                SectionCard(modifier = Modifier.animateCardEntrance(5), contentPadding = menuPadding) {
                    MenuItem(
                        title = "数据同步",
                        subtitle = "最后同步：今天 09:30",
                        leading = { MenuIcon(Icons.Default.Refresh, Primary100, MaterialTheme.colorScheme.primary) },
                        onClick = { viewModel.manualSync() },
                        trailing = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                            ) {
                                SyncBadge()
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(18.dp),
                                )
                            }
                        },
                        showArrow = false,
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                    )
                    MenuItem(
                        title = "导出数据",
                        subtitle = "导出为 JSON / CSV",
                        leading = { EmojiMenuIcon("\uD83D\uDCE5", MaterialTheme.colorScheme.surfaceVariant) },
                        onClick = { viewModel.exportData() },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                    )
                    MenuItem(
                        title = "清除缓存",
                        subtitle = "当前缓存 23.4 MB",
                        leading = { MenuIcon(Icons.Default.Delete, Color(0xFFFEF2F2), MaterialTheme.colorScheme.error) },
                        onClick = { viewModel.clearCache() },
                    )
                }

                // ── About section ────────────────────────────────────────
                SettingsSectionTitle(
                    title = "关于",
                    modifier = Modifier
                        .padding(top = MaterialTheme.spacing.xs)
                        .animateCardEntrance(6),
                )
                SectionCard(modifier = Modifier.animateCardEntrance(7), contentPadding = menuPadding) {
                    MenuItem(
                        title = "版本信息",
                        subtitle = "v1.0.0 (Build 42)",
                        leading = { MenuIcon(Icons.Default.Info, MaterialTheme.colorScheme.surfaceVariant) },
                        onClick = {},
                        showArrow = false,
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                    )
                    MenuItem(
                        title = "隐私政策",
                        leading = { EmojiMenuIcon("\uD83D\uDEE1\uFE0F", MaterialTheme.colorScheme.surfaceVariant) },
                        onClick = {
                            viewModel.showSnackbar("隐私政策将在正式版中提供")
                        },
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md),
                    )
                    MenuItem(
                        title = "用户协议",
                        leading = { EmojiMenuIcon("\uD83D\uDCC4", MaterialTheme.colorScheme.surfaceVariant) },
                        onClick = {
                            viewModel.showSnackbar("用户协议将在正式版中提供")
                        },
                    )
                }

                // ── Bottom actions ───────────────────────────────────────
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.sm))

                OutlinedButton(
                    onClick = { viewModel.requestLogout() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MaterialTheme.dimens.buttonHeight)
                        .animateCardEntrance(8),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(
                        width = 1.5.dp,
                        color = MaterialTheme.colorScheme.error,
                    ),
                ) {
                    Text(
                        text = "退出登录",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateCardEntrance(9),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "注销账户",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .clickable { viewModel.showDeleteAccountDialog() }
                            .padding(MaterialTheme.spacing.sm),
                    )
                }

                // Footer
                SettingsFooter(modifier = Modifier.animateCardEntrance(10))

                Spacer(modifier = Modifier.height(MaterialTheme.spacing.lg))
            }
        }
    }
}

/**
 * Section title with 3dp primary color vertical bar decoration.
 */
@Composable
private fun SettingsSectionTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(horizontal = MaterialTheme.spacing.xs),
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
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 0.5.sp,
        )
    }
}

@Composable
private fun MenuIcon(
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
private fun SyncBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Color(0xFFECFDF5))
            .padding(horizontal = MaterialTheme.spacing.sm, vertical = 2.dp),
    ) {
        Text(
            text = "已同步",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF059669),
        )
    }
}

@Composable
private fun SettingsFooter(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
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
