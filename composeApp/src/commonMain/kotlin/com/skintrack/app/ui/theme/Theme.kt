package com.skintrack.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

/**
 * ColorScheme wired from CSS §2 (light) and §3 (dark) semantic tokens.
 */
private val LightColors = lightColorScheme(
    // Interactive
    primary = Primary500,                   // --interactive-primary
    onPrimary = Neutral0,                   // --interactive-on-primary
    primaryContainer = Primary50,           // --surface-brand-subtle
    onPrimaryContainer = Primary900,

    // Secondary
    secondary = Secondary500,               // --secondary-500
    onSecondary = Neutral0,
    secondaryContainer = Secondary50,       // --secondary-50
    onSecondaryContainer = Secondary900,

    // Tertiary (rose accent)
    tertiary = Rose400,
    onTertiary = Neutral0,
    tertiaryContainer = Rose50,
    onTertiaryContainer = Rose900,

    // Error
    error = Error500,                        // --content-error (light)
    onError = Neutral0,
    errorContainer = Error50,                // --surface-error
    onErrorContainer = Error900,

    // Surface
    background = Neutral0,                   // --surface-primary
    onBackground = Neutral900,               // --content-primary
    surface = Neutral0,                      // --surface-primary
    onSurface = Neutral900,                  // --content-primary
    surfaceVariant = Neutral50,              // --surface-secondary
    onSurfaceVariant = Neutral600,           // --content-secondary
    surfaceContainerHighest = Neutral100,    // --surface-tertiary
    surfaceContainerHigh = Neutral50,
    surfaceContainer = Neutral50,
    surfaceContainerLow = Neutral0,
    surfaceContainerLowest = Neutral0,

    // Outline
    outline = Neutral200,                    // --border-default
    outlineVariant = Neutral100,             // --border-subtle

    // Inverse
    inverseSurface = Neutral900,             // --surface-inverse
    inverseOnSurface = Neutral0,             // --content-inverse
    inversePrimary = Primary300,

    // Scrim
    scrim = Color(0xFF0F1412),
)

private val DarkColors = darkColorScheme(
    // Interactive
    primary = Primary400,                    // --interactive-primary (dark)
    onPrimary = Neutral950,                  // --interactive-on-primary (dark)
    primaryContainer = Color(0xFF2A9D7C).copy(alpha = 0.12f), // --surface-brand-subtle
    onPrimaryContainer = Primary200,

    // Secondary
    secondary = Secondary400,
    onSecondary = Neutral950,
    secondaryContainer = Color(0xFF6B3B1F),
    onSecondaryContainer = Secondary100,

    // Tertiary (rose accent)
    tertiary = Rose400,
    onTertiary = Neutral950,
    tertiaryContainer = Color(0xFFF27A8E).copy(alpha = 0.10f),
    onTertiaryContainer = Rose100,

    // Error
    error = Error400,                         // --content-error (dark)
    onError = Error900,
    errorContainer = Color(0xFFEF4444).copy(alpha = 0.12f), // --surface-error (dark)
    onErrorContainer = Error200,

    // Surface — warm charcoal progression
    background = DarkSurfacePrimary,          // --surface-primary: #0F1412
    onBackground = DarkContentPrimary,        // --content-primary: #EAEFEC
    surface = DarkSurfacePrimary,             // #0F1412
    onSurface = DarkContentPrimary,           // #EAEFEC
    surfaceVariant = DarkSurfaceSecondary,    // --surface-secondary: #171D1A
    onSurfaceVariant = DarkContentSecondary,  // --content-secondary: #A0AAA5
    surfaceContainerHighest = DarkSurfaceTertiary, // #1E2522
    surfaceContainerHigh = DarkSurfaceSecondary,
    surfaceContainer = DarkSurfaceSecondary,
    surfaceContainerLow = DarkSurfacePrimary,
    surfaceContainerLowest = DarkSurfacePrimary,

    // Outline — translucent white per CSS §3
    outline = Color.White.copy(alpha = 0.10f),     // --border-default
    outlineVariant = Color.White.copy(alpha = 0.06f), // --border-subtle

    // Inverse
    inverseSurface = Neutral100,
    inverseOnSurface = Neutral900,
    inversePrimary = Primary700,

    // Scrim
    scrim = Color.Black,
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
        LocalAppTypography provides AppTypography(),
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

val MaterialTheme.appTypography: AppTypography
    @Composable get() = LocalAppTypography.current
