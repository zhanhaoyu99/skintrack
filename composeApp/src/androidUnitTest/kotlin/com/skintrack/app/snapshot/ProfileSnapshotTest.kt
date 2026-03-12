package com.skintrack.app.snapshot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import com.skintrack.app.ui.component.MenuItem
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfilePreview() {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("我的") })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = MaterialTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
        ) {
            // User info card
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.gradients.surface)
                        .padding(MaterialTheme.spacing.md),
                    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(MaterialTheme.dimens.avatarLarge)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(MaterialTheme.dimens.avatarIcon),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                    Column {
                        Text(
                            text = "user@example.com",
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "user@example.com",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            // Stats card
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(MaterialTheme.spacing.md),
                ) {
                    listOf(
                        "记录" to "12",
                        "产品" to "6",
                        "最新评分" to "82",
                        "连续打卡" to "5天",
                    ).forEach { (label, value) ->
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(
                                text = value,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center,
                            )
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }

            // Menu
            val trailing: @Composable () -> Unit = {
                Text(
                    text = ">",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column {
                    MenuItem(title = "会员中心", onClick = {}, trailing = trailing)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md))
                    MenuItem(title = "护肤品管理", onClick = {}, trailing = trailing)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md))
                    MenuItem(title = "归因分析报告", onClick = {}, trailing = trailing)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md))
                    MenuItem(title = "打卡提醒", onClick = {}, trailing = trailing)
                    HorizontalDivider(modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md))
                    MenuItem(title = "关于 SkinTrack", onClick = {}, trailing = trailing)
                }
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                MenuItem(
                    title = "退出登录",
                    onClick = {},
                    textColor = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
