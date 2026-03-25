package com.skintrack.app.ui.screen.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.skintrack.app.ui.component.animateCardEntrance
import com.skintrack.app.ui.component.animateFadeIn
import com.skintrack.app.ui.component.animateListItem
import com.skintrack.app.ui.screen.HomeScreen
import com.skintrack.app.ui.theme.Secondary300
import com.skintrack.app.ui.theme.Lavender300
import com.skintrack.app.ui.theme.Primary300
import com.skintrack.app.ui.theme.Motion
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.gradients
import com.skintrack.app.ui.theme.spacing
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

class AuthScreen : Screen {

    @Composable
    override fun Content() {
        val viewModel: AuthViewModel = koinViewModel()
        val uiState by viewModel.uiState.collectAsState()
        val navigator = LocalNavigator.currentOrThrow
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        // Focus management
        val passwordFocusRequester = remember { FocusRequester() }
        val confirmPasswordFocusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current

        // Password visibility
        var passwordVisible by remember { mutableStateOf(false) }
        var confirmPasswordVisible by remember { mutableStateOf(false) }

        // Validation states
        val emailTouched = remember { mutableStateOf(false) }
        val passwordTouched = remember { mutableStateOf(false) }
        val confirmPasswordTouched = remember { mutableStateOf(false) }

        val isEmailValid by remember {
            derivedStateOf { uiState.email.contains("@") && uiState.email.isNotBlank() }
        }
        val isPasswordValid by remember {
            derivedStateOf { uiState.password.length >= 6 }
        }
        val isConfirmPasswordValid by remember {
            derivedStateOf { uiState.confirmPassword == uiState.password && uiState.confirmPassword.isNotEmpty() }
        }

        val showEmailError by remember {
            derivedStateOf { emailTouched.value && !isEmailValid && uiState.email.isNotBlank() }
        }
        val showPasswordError by remember {
            derivedStateOf { passwordTouched.value && !isPasswordValid && uiState.password.isNotBlank() }
        }
        val showConfirmPasswordError by remember {
            derivedStateOf {
                confirmPasswordTouched.value && !isConfirmPasswordValid && uiState.confirmPassword.isNotBlank()
            }
        }

        val isFormValid by remember {
            derivedStateOf {
                if (uiState.isLoginMode) {
                    isEmailValid && isPasswordValid
                } else {
                    isEmailValid && isPasswordValid && isConfirmPasswordValid &&
                        uiState.nickname.isNotBlank()
                }
            }
        }

        val isEnabled = !uiState.isSubmitting && isFormValid

        val submitAction = {
            focusManager.clearFocus()
            viewModel.submit { navigator.replaceAll(HomeScreen()) }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .drawBehind {
                        // Decorative gradient circles
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Primary300.copy(alpha = 0.07f),
                                    Primary300.copy(alpha = 0.02f),
                                    Color.Transparent,
                                ),
                            ),
                            radius = 180.dp.toPx(),
                            center = Offset(size.width * 1.2f, -size.height * 0.07f),
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Secondary300.copy(alpha = 0.05f),
                                    Color.Transparent,
                                ),
                            ),
                            radius = 140.dp.toPx(),
                            center = Offset(-size.width * 0.13f, size.height * 0.88f),
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Lavender300.copy(alpha = 0.04f),
                                    Color.Transparent,
                                ),
                            ),
                            radius = 100.dp.toPx(),
                            center = Offset(size.width * 1.08f, size.height * 0.38f),
                        )
                    }
                    .imePadding(),
                contentAlignment = Alignment.Center,
            ) {
                val brandAlpha = remember { Animatable(0f) }
                val brandOffsetY = remember { Animatable(16f) }
                LaunchedEffect(Unit) {
                    brandAlpha.animateTo(1f, tween(Motion.LONG, easing = Motion.EmphasizedDecelerate))
                }
                LaunchedEffect(Unit) {
                    brandOffsetY.animateTo(0f, tween(Motion.LONG, easing = Motion.EmphasizedDecelerate))
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = MaterialTheme.spacing.xl),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
                ) {
                    // Brand area: logo + name + tagline grouped tightly
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .drawBehind {
                                    drawCircle(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                Primary300.copy(alpha = 0.12f),
                                                Color.Transparent,
                                            ),
                                        ),
                                        radius = 54.dp.toPx(),
                                    )
                                }
                                .clip(RoundedCornerShape(22.dp))
                                .background(brush = MaterialTheme.gradients.hero)
                                .graphicsLayer {
                                    alpha = brandAlpha.value
                                    translationY = brandOffsetY.value
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "\uD83C\uDF3F",
                                style = MaterialTheme.typography.headlineMedium,
                            )
                        }

                        // space-12 between logo and brand name
                        Box(modifier = Modifier.height(MaterialTheme.spacing.listGap))

                        Text(
                            text = "SkinTrack",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.8).sp,
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.graphicsLayer {
                                alpha = brandAlpha.value
                                translationY = brandOffsetY.value
                            },
                        )

                        // space-4 between brand name and tagline
                        Box(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (uiState.isLoginMode) "记录你的美，追踪你的变"
                                else "创建账号，开始你的护肤旅程",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .padding(bottom = MaterialTheme.spacing.sm)  // extra to reach ~24dp total before seg
                                .graphicsLayer {
                                    alpha = brandAlpha.value
                                },
                        )
                    }

                    // Segmented Control (Login / Register)
                    SegmentedControl(
                        isLoginMode = uiState.isLoginMode,
                        onModeChange = { viewModel.toggleMode() },
                        modifier = Modifier
                            .animateFadeIn(200),
                    )

                    // Nickname (register only)
                    AnimatedVisibility(
                        visible = !uiState.isLoginMode,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically(),
                    ) {
                        val nicknameMaxLength = 20
                        val isNicknameTooLong = uiState.nickname.length >= nicknameMaxLength
                        OutlinedTextField(
                            value = uiState.nickname,
                            onValueChange = { if (it.length <= nicknameMaxLength) viewModel.updateNickname(it) },
                            label = { Text("昵称") },
                            singleLine = true,
                            supportingText = {
                                Text(
                                    text = "${uiState.nickname.length}/$nicknameMaxLength",
                                    color = if (isNicknameTooLong) MaterialTheme.colorScheme.error
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Next,
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateListItem(2),
                        )
                    }

                    // Email
                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = {
                            viewModel.updateEmail(it)
                            emailTouched.value = true
                        },
                        label = { Text("邮箱") },
                        singleLine = true,
                        isError = showEmailError,
                        leadingIcon = {
                            Text(text = "\u2709\uFE0F", modifier = Modifier.padding(start = 4.dp))
                        },
                        supportingText = if (showEmailError) {
                            { Text("请输入有效邮箱") }
                        } else {
                            null
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next,
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { passwordFocusRequester.requestFocus() },
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateListItem(3),
                    )

                    // Password
                    OutlinedTextField(
                        value = uiState.password,
                        onValueChange = {
                            viewModel.updatePassword(it)
                            passwordTouched.value = true
                        },
                        label = { Text("密码") },
                        singleLine = true,
                        isError = showPasswordError,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                        },
                        supportingText = if (showPasswordError) {
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
                            imeAction = if (uiState.isLoginMode) ImeAction.Done else ImeAction.Next,
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (uiState.isLoginMode && isEnabled) submitAction()
                            },
                            onNext = { confirmPasswordFocusRequester.requestFocus() },
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(passwordFocusRequester)
                            .animateListItem(4),
                    )

                    // Confirm Password (register only)
                    AnimatedVisibility(
                        visible = !uiState.isLoginMode,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically(),
                    ) {
                        OutlinedTextField(
                            value = uiState.confirmPassword,
                            onValueChange = {
                                viewModel.updateConfirmPassword(it)
                                confirmPasswordTouched.value = true
                            },
                            label = { Text("确认密码") },
                            singleLine = true,
                            isError = showConfirmPasswordError,
                            supportingText = if (showConfirmPasswordError) {
                                { Text("两次输入的密码不一致") }
                            } else {
                                null
                            },
                            visualTransformation = if (confirmPasswordVisible) {
                                VisualTransformation.None
                            } else {
                                PasswordVisualTransformation()
                            },
                            trailingIcon = {
                                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                    Icon(
                                        imageVector = if (confirmPasswordVisible) {
                                            Icons.Default.VisibilityOff
                                        } else {
                                            Icons.Default.Visibility
                                        },
                                        contentDescription = if (confirmPasswordVisible) "隐藏密码" else "显示密码",
                                    )
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done,
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (isEnabled) submitAction()
                                },
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(confirmPasswordFocusRequester),
                        )
                    }

                    // Forgot password (login mode only)
                    AnimatedVisibility(
                        visible = uiState.isLoginMode,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically(),
                    ) {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            TextButton(
                                onClick = { navigator.push(ForgotPasswordScreen()) },
                                modifier = Modifier.align(Alignment.CenterEnd),
                            ) {
                                Text(
                                    text = "忘记密码？",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }

                    // Error
                    AnimatedVisibility(
                        visible = uiState.errorMessage != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically(),
                    ) {
                        Text(
                            text = uiState.errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }

                    // Submit
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(MaterialTheme.dimens.buttonHeight)
                            .animateCardEntrance(5)
                            .clip(RoundedCornerShape(50))
                            .background(
                                brush = MaterialTheme.gradients.primary,
                                alpha = if (isEnabled) 1f else 0.38f,
                            )
                            .then(
                                if (isEnabled) Modifier.clickable { submitAction() } else Modifier,
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        AnimatedContent(
                            targetState = uiState.isSubmitting,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                        ) { isSubmitting ->
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(MaterialTheme.dimens.buttonSpinnerSize),
                                    strokeWidth = MaterialTheme.dimens.buttonStrokeWidth,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            } else {
                                Text(
                                    text = if (uiState.isLoginMode) "登录" else "创建账号",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.labelLarge,
                                )
                            }
                        }
                    }

                    // Terms (register mode only) — shown below button
                    AnimatedVisibility(
                        visible = !uiState.isLoginMode,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically(),
                    ) {
                        val linkColor = MaterialTheme.colorScheme.primary
                        val textColor = MaterialTheme.colorScheme.onSurfaceVariant
                        val annotatedText = remember(linkColor, textColor) {
                            buildAnnotatedString {
                                withStyle(SpanStyle(color = textColor)) {
                                    append("注册即表示同意 ")
                                }
                                pushStringAnnotation("terms", "terms")
                                withStyle(
                                    SpanStyle(
                                        color = linkColor,
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                ) {
                                    append("服务条款")
                                }
                                pop()
                                withStyle(SpanStyle(color = textColor)) {
                                    append(" 和 ")
                                }
                                pushStringAnnotation("privacy", "privacy")
                                withStyle(
                                    SpanStyle(
                                        color = linkColor,
                                        fontWeight = FontWeight.SemiBold,
                                    ),
                                ) {
                                    append("隐私政策")
                                }
                                pop()
                            }
                        }
                        ClickableText(
                            text = annotatedText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                textAlign = TextAlign.Center,
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { offset ->
                                annotatedText.getStringAnnotations("privacy", offset, offset)
                                    .firstOrNull()?.let {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("隐私政策将在正式版中提供")
                                        }
                                    }
                                annotatedText.getStringAnnotations("terms", offset, offset)
                                    .firstOrNull()?.let {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("服务条款将在正式版中提供")
                                        }
                                    }
                            },
                        )
                    }

                    // Social login (login mode only) — V1 UI placeholder
                    AnimatedVisibility(
                        visible = uiState.isLoginMode,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically(),
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.cardInner),  // 20dp
                        ) {
                            // Divider with "或"
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.listGap),  // 12dp
                            ) {
                                HorizontalDivider(modifier = Modifier.weight(1f))
                                Text(
                                    text = "或",
                                    style = MaterialTheme.typography.labelMedium,  // c1 = 12sp/500
                                    color = MaterialTheme.colorScheme.outline,  // content-disabled
                                )
                                HorizontalDivider(modifier = Modifier.weight(1f))
                            }

                            // Social buttons
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(
                                    MaterialTheme.spacing.listGap,  // 12dp
                                ),
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                val comingSoonAction = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("即将上线，敬请期待")
                                    }
                                    Unit
                                }
                                SocialLoginButton(
                                    label = "Apple",
                                    onClick = comingSoonAction,
                                    modifier = Modifier.weight(1f),
                                )
                                SocialLoginButton(
                                    label = "微信",
                                    onClick = comingSoonAction,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                    }

                    // Trust badges — vary by mode per design spec
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = MaterialTheme.spacing.md),
                        horizontalArrangement = Arrangement.spacedBy(
                            MaterialTheme.spacing.cardInner,  // 20dp per mockup
                            Alignment.CenterHorizontally,
                        ),
                    ) {
                        if (uiState.isLoginMode) {
                            TrustBadge(emoji = "\uD83D\uDEE1\uFE0F", label = "隐私保护")
                            TrustBadge(emoji = "\u2705", label = "10万+用户")
                            TrustBadge(icon = Icons.Default.Star, label = "4.8 评分")
                        } else {
                            TrustBadge(emoji = "\uD83D\uDD12", label = "数据加密")
                            TrustBadge(emoji = "\u2705", label = "随时取消")
                        }
                    }
                }
            }

                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )
            }
        }
    }
}

@Composable
private fun SegmentedControl(
    isLoginMode: Boolean,
    onModeChange: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)  // radius-md = 12dp
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
    ) {
        SegmentButton(
            text = "登录",
            isSelected = isLoginMode,
            onClick = { if (!isLoginMode) onModeChange() },
            modifier = Modifier.weight(1f),
        )
        SegmentButton(
            text = "注册",
            isSelected = !isLoginMode,
            onClick = { if (isLoginMode) onModeChange() },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SegmentButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)  // radius-sm = 8dp
            .clickable(onClick = onClick),
        color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
        shadowElevation = if (isSelected) 1.dp else 0.dp,
        shape = MaterialTheme.shapes.small,
    ) {
        Box(
            modifier = Modifier.padding(vertical = MaterialTheme.spacing.compact),  // 10dp
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,  // b2 = 14sp
                fontWeight = FontWeight.SemiBold,  // 600 always per mockup
                color = if (isSelected) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SocialLoginButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(MaterialTheme.dimens.buttonHeight)  // button-height = 52dp
            .clip(RoundedCornerShape(50))  // radius-full (pill)
            .border(1.5.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.sm),  // 8dp
        ) {
            Text(
                text = label.first().toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,  // b2 = 14sp
                fontWeight = FontWeight.SemiBold,  // 600
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun TrustBadge(
    label: String,
    icon: ImageVector? = null,
    emoji: String? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),  // space-4
    ) {
        if (emoji != null) {
            Text(text = emoji, fontSize = 16.sp)  // icon-size-xs = 16dp
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,  // content-disabled
                modifier = Modifier.size(16.dp),  // icon-size-xs
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,  // c2 = 10sp
            fontWeight = FontWeight.Medium,  // 500
            color = MaterialTheme.colorScheme.outline,  // content-disabled
        )
    }
}
