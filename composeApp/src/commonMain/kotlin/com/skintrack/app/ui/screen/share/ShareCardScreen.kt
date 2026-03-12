package com.skintrack.app.ui.screen.share

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
                    title = { Text("分享卡片") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                        }
                    },
                )
            },
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
                        onUpgrade = { navigator.push(PaywallScreen()) },
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
    onUpgrade: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing

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

        // Share button or locked card
        if (state.canShare) {
            Button(
                onClick = onShare,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("分享")
            }
        } else {
            LockedFeatureCard(
                message = FeatureGate.SHARE_CARD.lockedMessage,
                onUpgrade = onUpgrade,
            )
        }
    }
}
