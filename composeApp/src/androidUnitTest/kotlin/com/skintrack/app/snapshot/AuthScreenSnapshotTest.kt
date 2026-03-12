package com.skintrack.app.snapshot

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import com.skintrack.app.ui.theme.Motion
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.gradients
import com.skintrack.app.ui.theme.spacing
import androidx.compose.material3.Surface
import org.junit.Test

class AuthScreenSnapshotTest : SnapshotTestBase() {

    @Test
    fun authScreen_login_light() = captureLight {
        AuthScreenPreview(isLoginMode = true)
    }

    @Test
    fun authScreen_register_light() = captureLight {
        AuthScreenPreview(isLoginMode = false)
    }

    @Test
    fun authScreen_login_dark() = captureDark {
        AuthScreenPreview(isLoginMode = true)
    }
}

@androidx.compose.runtime.Composable
private fun AuthScreenPreview(isLoginMode: Boolean) {
    Surface(modifier = Modifier.fillMaxSize()) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = MaterialTheme.spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
        ) {
            Text(
                text = "SkinTrack",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "记录你的皮肤变化",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Row(
                modifier = Modifier.padding(top = MaterialTheme.spacing.md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(onClick = {}) {
                    Text(
                        text = "登录",
                        color = if (isLoginMode) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(text = "|", color = MaterialTheme.colorScheme.onSurfaceVariant)
                TextButton(onClick = {}) {
                    Text(
                        text = "注册",
                        color = if (!isLoginMode) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            OutlinedTextField(
                value = "user@example.com",
                onValueChange = {},
                label = { Text("邮箱") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedTextField(
                value = "••••••",
                onValueChange = {},
                label = { Text("密码") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(MaterialTheme.dimens.buttonHeight)
                    .clip(MaterialTheme.shapes.large)
                    .background(brush = MaterialTheme.gradients.primary),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = if (isLoginMode) "登录" else "注册",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
    }
}
