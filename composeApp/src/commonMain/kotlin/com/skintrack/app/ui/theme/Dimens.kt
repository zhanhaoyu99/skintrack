package com.skintrack.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class Dimens(
    val buttonHeight: Dp = 52.dp,
    val inputHeight: Dp = 50.dp,
    val buttonSpinnerSize: Dp = 20.dp,
    val buttonStrokeWidth: Dp = 2.dp,
    val avatarLarge: Dp = 56.dp,
    val avatarExtraLarge: Dp = 64.dp,
    val avatarIcon: Dp = 32.dp,
    val menuIconSize: Dp = 32.dp,
    val captureButtonSize: Dp = 72.dp,
    val captureButtonBorder: Dp = 4.dp,
    val captureButtonInnerPadding: Dp = 6.dp,
    val thumbnailSize: Dp = 72.dp,
    val photoCompareHeight: Dp = 160.dp,
    val chartHeight: Dp = 200.dp,
    val chartDotRadius: Dp = 4.dp,
    val chartLineWidth: Dp = 2.dp,
    val scoreBarHeight: Dp = 8.dp,
    val iconSmall: Dp = 20.dp,
    val iconMedium: Dp = 24.dp,
    val iconLarge: Dp = 32.dp,
)

val LocalDimens = staticCompositionLocalOf { Dimens() }
