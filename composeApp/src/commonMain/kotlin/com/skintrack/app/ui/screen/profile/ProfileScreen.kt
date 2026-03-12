package com.skintrack.app.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.skintrack.app.domain.model.AuthUser
import com.skintrack.app.ui.component.LoadingContent
import com.skintrack.app.ui.component.MenuItem
import com.skintrack.app.ui.screen.attribution.AttributionReportScreen
import com.skintrack.app.ui.screen.auth.AuthScreen
import com.skintrack.app.ui.screen.product.ProductManageScreen
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.gradients
import com.skintrack.app.ui.theme.spacing
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val authUser by viewModel.authUser.collectAsState()
    val navigator = LocalNavigator.currentOrThrow

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text("我的") })

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
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = MaterialTheme.spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
                ) {
                    UserInfoCard(authUser)
                    StatsCard(state)
                    MenuSection(
                        onProductManage = { navigator.push(ProductManageScreen()) },
                        onAttributionReport = { navigator.push(AttributionReportScreen()) },
                        onLogout = {
                            viewModel.logout()
                            navigator.replaceAll(AuthScreen())
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun UserInfoCard(authUser: AuthUser?) {
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
                    text = authUser?.displayName ?: authUser?.email ?: "本地用户",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = if (authUser != null) authUser.email else "登录后同步数据",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun StatsCard(state: ProfileUiState.Content) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.md),
        ) {
            StatItem(
                label = "记录",
                value = "${state.totalRecords}",
                modifier = Modifier.weight(1f),
            )
            StatItem(
                label = "产品",
                value = "${state.totalProducts}",
                modifier = Modifier.weight(1f),
            )
            StatItem(
                label = "最新评分",
                value = state.latestScore?.toString() ?: "--",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
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

@Composable
private fun MenuSection(
    onProductManage: () -> Unit,
    onAttributionReport: () -> Unit,
    onLogout: () -> Unit,
) {
    val menuTrailing: @Composable () -> Unit = {
        Text(
            text = ">",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column {
            MenuItem(title = "护肤品管理", onClick = onProductManage, trailing = menuTrailing)
            HorizontalDivider(modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md))
            MenuItem(title = "归因分析报告", onClick = onAttributionReport, trailing = menuTrailing)
            HorizontalDivider(modifier = Modifier.padding(horizontal = MaterialTheme.spacing.md))
            MenuItem(title = "关于 SkinTrack", onClick = {}, trailing = menuTrailing)
        }
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        MenuItem(
            title = "退出登录",
            onClick = onLogout,
            textColor = MaterialTheme.colorScheme.error,
        )
    }
}
