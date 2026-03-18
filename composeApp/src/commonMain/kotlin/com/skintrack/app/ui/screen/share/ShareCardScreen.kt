package com.skintrack.app.ui.screen.share

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.skintrack.app.domain.model.FeatureGate
import com.skintrack.app.domain.model.lockedMessage
import com.skintrack.app.ui.component.LoadingContent
import com.skintrack.app.ui.component.LockedFeatureCard
import com.skintrack.app.ui.screen.paywall.PaywallScreen
import com.skintrack.app.ui.theme.spacing
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

data class ShareCardScreen(
    val beforeId: String? = null,
    val afterId: String? = null,
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: ShareCardViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(Unit) {
            if (beforeId != null && afterId != null) {
                viewModel.loadRecords(beforeId, afterId)
            } else {
                viewModel.loadLatestCompare()
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("分享对比卡片") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                        }
                    },
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { padding ->
            when (val state = uiState) {
                is ShareCardUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center,
                    ) {
                        LoadingContent()
                    }
                }
                is ShareCardUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                is ShareCardUiState.Content -> {
                    ShareCardContentView(
                        state = state,
                        onShare = viewModel::share,
                        onSaveImage = viewModel::share, // V1: same as share
                        onUpgrade = { navigator.push(PaywallScreen()) },
                        snackbarHostState = snackbarHostState,
                        modifier = Modifier.padding(padding),
                    )
                }
            }
        }
    }
}

@Composable
private fun ShareCardContentView(
    state: ShareCardUiState.Content,
    onShare: () -> Unit,
    onSaveImage: () -> Unit,
    onUpgrade: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.lg),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        // Card preview
        ShareCardContent(
            before = state.before,
            after = state.after,
        )

        // Template selector
        TemplateSelector(
            onUnavailableClick = {
                scope.launch {
                    snackbarHostState.showSnackbar("更多模板即将上线")
                }
            },
        )

        // Share targets
        ShareTargets(
            onTargetClick = {
                scope.launch {
                    snackbarHostState.showSnackbar("即将上线，敬请期待")
                }
            },
        )

        // Bottom buttons or locked card
        if (state.canShare) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                OutlinedButton(
                    onClick = onSaveImage,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("保存图片")
                }
                Button(
                    onClick = onShare,
                    modifier = Modifier.weight(1f),
                ) {
                    Text("分享")
                }
            }
        } else {
            LockedFeatureCard(
                message = FeatureGate.SHARE_CARD.lockedMessage,
                onUpgrade = onUpgrade,
            )
        }
    }
}
