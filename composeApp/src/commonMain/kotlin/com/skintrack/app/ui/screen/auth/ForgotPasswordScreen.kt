package com.skintrack.app.ui.screen.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.skintrack.app.ui.component.animateListItem
import com.skintrack.app.ui.theme.Primary50
import com.skintrack.app.ui.theme.Primary100
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.gradients
import com.skintrack.app.ui.theme.spacing
import org.koin.compose.viewmodel.koinViewModel

class ForgotPasswordScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel: ForgotPasswordViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("找回密码") },
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
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = MaterialTheme.spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
            ) {
                AnimatedContent(
                    targetState = uiState.step,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                        } else {
                            slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                        }
                    },
                ) { step ->
                    when (step) {
                        1 -> EmailStep(
                            email = uiState.email,
                            isLoading = uiState.isLoading,
                            error = uiState.error,
                            onEmailChange = viewModel::updateEmail,
                            onSubmit = viewModel::requestReset,
                            onBackToLogin = { navigator.pop() },
                        )
                        2 -> CodeAndPasswordStep(
                            code = uiState.code,
                            newPassword = uiState.newPassword,
                            isLoading = uiState.isLoading,
                            error = uiState.error,
                            onCodeChange = viewModel::updateCode,
                            onPasswordChange = viewModel::updateNewPassword,
                            onSubmit = viewModel::verifyAndReset,
                        )
                        3 -> SuccessStep(
                            onBackToLogin = { navigator.pop() },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmailStep(
    email: String,
    isLoading: Boolean,
    error: String?,
    onEmailChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onBackToLogin: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val isValid = email.contains("@") && email.isNotBlank()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
    ) {
        // Lock icon with gradient background
        Box(
            modifier = Modifier
                .padding(top = MaterialTheme.spacing.lg)  // space-32 top padding in mockup
                .size(88.dp)
                .clip(CircleShape)  // radius-full
                .background(brush = MaterialTheme.gradients.primary)  // grad-primary
                .animateListItem(0),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp),
            )
        }

        // Title
        Text(
            text = "重置密码",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,  // 800 per mockup
            ),
            modifier = Modifier.animateListItem(1),
        )

        // Description
        Text(
            text = "输入你注册时使用的邮箱，我们会发送重置链接",
            style = MaterialTheme.typography.bodyMedium,  // b2
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.animateListItem(2),
        )

        // Info hint box with left border accent
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)  // radius-md = 12dp
                .background(Primary50)  // surface-brand-subtle
                .drawBehind {
                    // Left border accent (3px)
                    drawRect(
                        color = Color(0xFF2A9D7C),  // interactive-primary
                        size = size.copy(width = 3.dp.toPx()),
                    )
                }
                .padding(MaterialTheme.spacing.listGap)  // 12dp
                .animateListItem(3),
        ) {
            Text(
                text = "重置链接将在 24 小时内有效，请及时查收邮件并完成密码修改",
                style = MaterialTheme.typography.bodySmall,  // b3 = 13sp
                color = MaterialTheme.colorScheme.primary,  // content-brand
                lineHeight = 19.5.sp,  // 13 * 1.5
            )
        }

        // Email input
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("邮箱") },
            placeholder = { Text("你的邮箱地址") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (isValid && !isLoading) onSubmit()
                },
            ),
            modifier = Modifier
                .fillMaxWidth()
                .animateListItem(4),
        )

        ErrorMessage(error)

        SubmitButton(
            text = "发送重置链接",
            isLoading = isLoading,
            isEnabled = isValid && !isLoading,
            onClick = {
                focusManager.clearFocus()
                onSubmit()
            },
            modifier = Modifier.animateListItem(5),
        )

        // Back to login link
        Text(
            text = "返回登录",
            style = MaterialTheme.typography.labelLarge,  // btn token
            color = MaterialTheme.colorScheme.onSurfaceVariant,  // content-tertiary
            modifier = Modifier
                .clickable { onBackToLogin() }
                .animateListItem(6),
        )
    }
}

@Composable
private fun CodeAndPasswordStep(
    code: String,
    newPassword: String,
    isLoading: Boolean,
    error: String?,
    onCodeChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    var passwordVisible by remember { mutableStateOf(false) }
    val isValid = code.length == 6 && newPassword.length >= 6

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
    ) {
        Text(
            text = "输入邮箱收到的 6 位验证码和新密码",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .animateListItem(0),
        )

        OutlinedTextField(
            value = code,
            onValueChange = onCodeChange,
            label = { Text("验证码") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .animateListItem(2),
        )

        OutlinedTextField(
            value = newPassword,
            onValueChange = onPasswordChange,
            label = { Text("新密码") },
            singleLine = true,
            supportingText = if (newPassword.isNotEmpty() && newPassword.length < 6) {
                { Text("密码至少需要6个字符") }
            } else {
                null
            },
            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) {
                            Icons.Default.VisibilityOff
                        } else {
                            Icons.Default.Visibility
                        },
                        contentDescription = if (passwordVisible) "隐藏密码" else "显示密码",
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (isValid && !isLoading) onSubmit()
                },
            ),
            modifier = Modifier
                .fillMaxWidth()
                .animateListItem(3),
        )

        ErrorMessage(error)

        SubmitButton(
            text = "重置密码",
            isLoading = isLoading,
            isEnabled = isValid && !isLoading,
            onClick = {
                focusManager.clearFocus()
                onSubmit()
            },
            modifier = Modifier.animateListItem(4),
        )
    }
}

@Composable
private fun SuccessStep(
    onBackToLogin: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.lg),
    ) {
        // Success icon
        Box(
            modifier = Modifier
                .padding(vertical = MaterialTheme.spacing.md)
                .size(88.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Primary100, Primary50),
                    ),
                )
                .animateListItem(0),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "✓",
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        Text(
            text = "密码重置成功",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.animateListItem(1),
        )

        Text(
            text = "你现在可以使用新密码登录了",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.animateListItem(2),
        )

        SubmitButton(
            text = "返回登录",
            isLoading = false,
            isEnabled = true,
            onClick = onBackToLogin,
            modifier = Modifier.animateListItem(3),
        )
    }
}

@Composable
private fun ErrorMessage(error: String?) {
    AnimatedVisibility(
        visible = error != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        Text(
            text = error ?: "",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun SubmitButton(
    text: String,
    isLoading: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(MaterialTheme.dimens.buttonHeight)
            .clip(RoundedCornerShape(50))  // radius-full (pill)
            .background(
                brush = MaterialTheme.gradients.primary,
                alpha = if (isEnabled) 1f else 0.38f,
            )
            .then(
                if (isEnabled) Modifier.clickable(onClick = onClick) else Modifier,
            ),
        contentAlignment = Alignment.Center,
    ) {
        AnimatedContent(
            targetState = isLoading,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
        ) { loading ->
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(MaterialTheme.dimens.buttonSpinnerSize),
                    strokeWidth = MaterialTheme.dimens.buttonStrokeWidth,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            } else {
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}
