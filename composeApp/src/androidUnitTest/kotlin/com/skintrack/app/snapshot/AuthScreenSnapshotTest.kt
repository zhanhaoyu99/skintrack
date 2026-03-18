package com.skintrack.app.snapshot

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.skintrack.app.ui.theme.dimens
import com.skintrack.app.ui.theme.gradients
import com.skintrack.app.ui.theme.spacing
import org.junit.Test

class AuthScreenSnapshotTest : SnapshotTestBase() {

    @Test
    fun authScreen_login_light() = captureLight {
        AuthScreenPreview(isLoginMode = true)
    }

    @Test
    fun authScreen_login_dark() = captureDark {
        AuthScreenPreview(isLoginMode = true)
    }

    @Test
    fun authScreen_register_light() = captureLight {
        AuthScreenPreview(isLoginMode = false)
    }
}

@Composable
private fun AuthScreenPreview(isLoginMode: Boolean) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = MaterialTheme.spacing.xl),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
            ) {
                // Brand logo area — 72dp, 22dp radius, hero gradient
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(brush = MaterialTheme.gradients.hero),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "\uD83C\uDF3F",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                }

                Text(
                    text = "SkinTrack",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "\u5F00\u542F\u4F60\u7684\u7F8E\u4E3D\u65C5\u7A0B",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                // Segmented Control
                SegmentedControl(
                    isLoginMode = isLoginMode,
                    modifier = Modifier.padding(top = MaterialTheme.spacing.sm),
                )

                // Nickname (register only)
                if (!isLoginMode) {
                    OutlinedTextField(
                        value = "\u5C0F\u660E",
                        onValueChange = {},
                        label = { Text("\u6635\u79F0") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                // Email
                OutlinedTextField(
                    value = "user@example.com",
                    onValueChange = {},
                    label = { Text("\u90AE\u7BB1") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                // Password
                OutlinedTextField(
                    value = "password",
                    onValueChange = {},
                    label = { Text("\u5BC6\u7801") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = "\u663E\u793A\u5BC6\u7801",
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )

                // Confirm Password (register only)
                if (!isLoginMode) {
                    OutlinedTextField(
                        value = "password",
                        onValueChange = {},
                        label = { Text("\u786E\u8BA4\u5BC6\u7801") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = {}) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = "\u663E\u793A\u5BC6\u7801",
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                // Forgot password (login mode only)
                if (isLoginMode) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        TextButton(
                            onClick = {},
                            modifier = Modifier.align(Alignment.CenterEnd),
                        ) {
                            Text(
                                text = "\u5FD8\u8BB0\u5BC6\u7801\uFF1F",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }

                // Submit button with gradient — pill shape
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(MaterialTheme.dimens.buttonHeight)
                        .clip(RoundedCornerShape(50))
                        .background(brush = MaterialTheme.gradients.primary),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = if (isLoginMode) "\u767B\u5F55" else "\u5F00\u59CB\u62A4\u80A4\u4E4B\u65C5",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }

                // Terms (register mode only)
                if (!isLoginMode) {
                    val linkColor = MaterialTheme.colorScheme.primary
                    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
                    val annotatedText = buildAnnotatedString {
                        withStyle(SpanStyle(color = textColor)) {
                            append("\u6CE8\u518C\u5373\u8868\u793A\u540C\u610F ")
                        }
                        withStyle(
                            SpanStyle(
                                color = linkColor,
                                fontWeight = FontWeight.SemiBold,
                            ),
                        ) {
                            append("\u670D\u52A1\u6761\u6B3E")
                        }
                        withStyle(SpanStyle(color = textColor)) {
                            append(" \u548C ")
                        }
                        withStyle(
                            SpanStyle(
                                color = linkColor,
                                fontWeight = FontWeight.SemiBold,
                            ),
                        ) {
                            append("\u9690\u79C1\u653F\u7B56")
                        }
                    }
                    Text(
                        text = annotatedText,
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                // Social login (login mode only)
                if (isLoginMode) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
                    ) {
                        // Divider with "or"
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = MaterialTheme.spacing.xs),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md),
                        ) {
                            HorizontalDivider(modifier = Modifier.weight(1f))
                            Text(
                                text = "\u6216",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            HorizontalDivider(modifier = Modifier.weight(1f))
                        }

                        // Social buttons as rectangular cards
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            SocialLoginButton(
                                label = "Apple",
                                modifier = Modifier.weight(1f),
                            )
                            SocialLoginButton(
                                label = "WeChat",
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }

                // Trust badges — vary by mode
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = MaterialTheme.spacing.md),
                    horizontalArrangement = Arrangement.spacedBy(
                        18.dp,
                        Alignment.CenterHorizontally,
                    ),
                ) {
                    if (isLoginMode) {
                        TrustBadge(icon = Icons.Default.Lock, label = "\u9690\u79C1\u5B89\u5168")
                        TrustBadge(icon = Icons.Default.Person, label = "10\u4E07+\u7528\u6237")
                        TrustBadge(icon = Icons.Default.Star, label = "4.8\u5206\u597D\u8BC4")
                    } else {
                        TrustBadge(icon = Icons.Default.Lock, label = "\u6570\u636E\u52A0\u5BC6")
                        TrustBadge(icon = Icons.Default.Person, label = "\u968F\u65F6\u6CE8\u9500")
                    }
                }
            }
        }
    }
}

@Composable
private fun SegmentedControl(
    isLoginMode: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(3.dp),
    ) {
        SegmentButton(
            text = "\u767B\u5F55",
            isSelected = isLoginMode,
            modifier = Modifier.weight(1f),
        )
        SegmentButton(
            text = "\u6CE8\u518C",
            isSelected = !isLoginMode,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun SegmentButton(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(50)),
        color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
        shadowElevation = if (isSelected) 1.dp else 0.dp,
        shape = RoundedCornerShape(50),
    ) {
        Box(
            modifier = Modifier.padding(vertical = 11.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.onSurface
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SocialLoginButton(
    label: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(50.dp)
            .clip(MaterialTheme.shapes.medium)
            .border(1.5.dp, MaterialTheme.colorScheme.outlineVariant, MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = label.first().toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun TrustBadge(
    icon: ImageVector,
    label: String,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
