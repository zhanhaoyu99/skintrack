package com.skintrack.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Mint400,
    onPrimary = Color.White,
    primaryContainer = Mint100,
    onPrimaryContainer = Mint700,
    secondary = Apricot300,
    onSecondary = Color.White,
    secondaryContainer = Apricot100,
    onSecondaryContainer = Apricot500,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnBackgroundLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    error = ErrorLight,
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = Mint300,
    onPrimary = Mint700,
    primaryContainer = Mint600,
    onPrimaryContainer = Mint100,
    secondary = Apricot200,
    onSecondary = Apricot500,
    secondaryContainer = Apricot400,
    onSecondaryContainer = Apricot50,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnBackgroundDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    error = ErrorDark,
    onError = OnErrorDark,
)

@Composable
fun SkinTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors
    val gradients = if (darkTheme) DarkGradientColors else LightGradientColors

    CompositionLocalProvider(
        LocalExtendedColors provides extendedColors,
        LocalGradientColors provides gradients,
        LocalSpacing provides Spacing(),
        LocalDimens provides Dimens(),
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = SkinTrackTypography,
            shapes = SkinTrackShapes,
            content = content,
        )
    }
}

// ── Extension properties for convenient access ──────────────────────

val MaterialTheme.extendedColors: ExtendedColors
    @Composable get() = LocalExtendedColors.current

val MaterialTheme.spacing: Spacing
    @Composable get() = LocalSpacing.current

val MaterialTheme.gradients: GradientColors
    @Composable get() = LocalGradientColors.current

val MaterialTheme.dimens: Dimens
    @Composable get() = LocalDimens.current
