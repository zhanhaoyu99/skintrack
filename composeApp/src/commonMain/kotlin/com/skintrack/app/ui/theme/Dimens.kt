package com.skintrack.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 1:1 mapping to CSS §10 component-level tokens.
 */
@Immutable
data class Dimens(
    // Buttons
    val buttonHeight: Dp = 52.dp,        // --button-height
    val buttonHeightSm: Dp = 40.dp,      // --button-height-sm
    val buttonSpinnerSize: Dp = 20.dp,
    val buttonStrokeWidth: Dp = 2.dp,

    // Inputs
    val inputHeight: Dp = 52.dp,         // --input-height

    // Icons
    val iconXs: Dp = 16.dp,             // --icon-size-xs
    val iconSmall: Dp = 20.dp,          // --icon-size-sm
    val iconMedium: Dp = 24.dp,         // --icon-size-md
    val iconLarge: Dp = 32.dp,          // --icon-size-lg
    val iconXl: Dp = 40.dp,             // --icon-size-xl

    // Avatars
    val avatarSm: Dp = 36.dp,           // --avatar-sm
    val avatarMd: Dp = 44.dp,           // --avatar-md
    val avatarLarge: Dp = 56.dp,        // --avatar-lg
    val avatarExtraLarge: Dp = 72.dp,   // --avatar-xl

    // Navigation
    val bottomNavHeight: Dp = 84.dp,     // --bottom-nav-height
    val statusBarHeight: Dp = 54.dp,     // --status-bar-height
    val topBarHeight: Dp = 56.dp,        // --top-bar-height
    val fabSize: Dp = 52.dp,             // --fab-size

    // Cards
    val cardBorderWidth: Dp = 0.5.dp,    // --card-border-width

    // Thumbnails
    val thumbnailSm: Dp = 48.dp,        // --thumbnail-sm
    val thumbnailMd: Dp = 64.dp,        // --thumbnail-md
    val thumbnailLg: Dp = 80.dp,        // --thumbnail-lg

    // Menu item icon wrap
    val menuIconWrap: Dp = 38.dp,        // menu-item .icon-wrap (38px)
    val menuIconRadius: Dp = 11.dp,      // menu-item .icon-wrap border-radius (11px)

    // Custom (non-CSS, app-specific)
    val avatarIcon: Dp = 32.dp,
    val captureButtonSize: Dp = 72.dp,
    val captureButtonBorder: Dp = 4.dp,
    val captureButtonInnerPadding: Dp = 6.dp,
    val thumbnailSize: Dp = 72.dp,
    val photoCompareHeight: Dp = 160.dp,
    val chartHeight: Dp = 200.dp,
    val chartDotRadius: Dp = 4.dp,
    val chartLineWidth: Dp = 2.dp,
    val scoreBarHeight: Dp = 8.dp,
    val menuIconSize: Dp = 32.dp,
)

val LocalDimens = staticCompositionLocalOf { Dimens() }
