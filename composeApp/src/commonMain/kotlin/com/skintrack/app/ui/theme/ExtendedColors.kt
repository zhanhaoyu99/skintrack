package com.skintrack.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class SkinMetricColors(
    val acne: Color,
    val pore: Color,
    val evenness: Color,
    val hydration: Color,
    val redness: Color,
)

@Immutable
data class FunctionalColors(
    val success: Color,
    val successBg: Color,
    val warning: Color,
    val warningBg: Color,
    val error: Color,
    val errorBg: Color,
    val info: Color,
    val infoBg: Color,
)

@Immutable
data class CameraColors(
    val overlay: Color,
    val guideBorder: Color,
    val buttonBorder: Color,
    val buttonFill: Color,
    val buttonDisabled: Color,
    val background: Color,
)

@Immutable
data class ContentColors(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val disabled: Color,
    val inverse: Color,
    val brand: Color,
)

@Immutable
data class SurfaceColors(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val elevated: Color,
    val inverse: Color,
    val brand: Color,
    val brandSubtle: Color,
    val rose: Color,
    val lavender: Color,
)

@Immutable
data class BorderColors(
    val default: Color,
    val subtle: Color,
    val strong: Color,
    val brand: Color,
)

@Immutable
data class InteractiveColors(
    val primary: Color,
    val primaryHover: Color,
    val primaryPressed: Color,
    val primaryDisabled: Color,
    val secondary: Color,
    val onPrimary: Color,
)

@Immutable
data class OverlayColors(
    val light: Color,
    val dark: Color,
    val scrim: Color,
)

@Immutable
data class ExtendedColors(
    val skinMetric: SkinMetricColors,
    val functional: FunctionalColors,
    val camera: CameraColors,
    val content: ContentColors,
    val surface: SurfaceColors,
    val border: BorderColors,
    val interactive: InteractiveColors,
    val overlay: OverlayColors,
)

// ── Light Theme ─────────────────────────────────────────────────

val LightSkinMetricColors = SkinMetricColors(
    acne = MetricAcneLight,
    pore = MetricPoreLight,
    evenness = MetricEvennessLight,
    hydration = MetricHydrationLight,
    redness = MetricRednessLight,
)

val LightFunctionalColors = FunctionalColors(
    success = Success500,
    successBg = Success50,
    warning = Warning500,
    warningBg = Warning50,
    error = Error500,
    errorBg = Error50,
    info = Info500,
    infoBg = Info50,
)

val LightContentColors = ContentColors(
    primary = Neutral900,
    secondary = Neutral600,
    tertiary = Neutral500,
    disabled = Neutral400,
    inverse = Neutral0,
    brand = Primary500,
)

val LightSurfaceColors = SurfaceColors(
    primary = Neutral0,
    secondary = Neutral50,
    tertiary = Neutral100,
    elevated = Neutral0,
    inverse = Neutral900,
    brand = Primary500,
    brandSubtle = Primary50,
    rose = Rose50,
    lavender = Lavender50,
)

val LightBorderColors = BorderColors(
    default = Neutral200,
    subtle = Neutral100,
    strong = Neutral400,
    brand = Primary400,
)

val LightInteractiveColors = InteractiveColors(
    primary = Primary500,
    primaryHover = Primary600,
    primaryPressed = Primary700,
    primaryDisabled = Primary200,
    secondary = Neutral100,
    onPrimary = Neutral0,
)

val LightOverlayColors = OverlayColors(
    light = Color(0xFFFFFFFF).copy(alpha = 0.88f),
    dark = Color(0xFF0F1412).copy(alpha = 0.5f),
    scrim = Color(0xFF0F1412).copy(alpha = 0.32f),
)

val DefaultCameraColors = CameraColors(
    overlay = CameraOverlay,
    guideBorder = CameraGuideBorder,
    buttonBorder = CameraButtonBorder,
    buttonFill = CameraButtonFill,
    buttonDisabled = CameraButtonDisabled,
    background = CameraBackground,
)

val LightExtendedColors = ExtendedColors(
    skinMetric = LightSkinMetricColors,
    functional = LightFunctionalColors,
    camera = DefaultCameraColors,
    content = LightContentColors,
    surface = LightSurfaceColors,
    border = LightBorderColors,
    interactive = LightInteractiveColors,
    overlay = LightOverlayColors,
)

// ── Dark Theme ──────────────────────────────────────────────────

val DarkSkinMetricColors = SkinMetricColors(
    acne = MetricAcneDark,
    pore = MetricPoreDark,
    evenness = MetricEvennessDark,
    hydration = MetricHydrationDark,
    redness = MetricRednessDark,
)

val DarkFunctionalColors = FunctionalColors(
    success = Success400,
    successBg = Color(0xFF10B981).copy(alpha = 0.12f),
    warning = Warning400,
    warningBg = Color(0xFFF59E0B).copy(alpha = 0.12f),
    error = Error400,
    errorBg = Color(0xFFEF4444).copy(alpha = 0.12f),
    info = Info400,
    infoBg = Color(0xFF3B82F6).copy(alpha = 0.12f),
)

val DarkContentColors = ContentColors(
    primary = DarkContentPrimary,
    secondary = DarkContentSecondary,
    tertiary = DarkContentTertiary,
    disabled = DarkContentDisabled,
    inverse = Neutral900,
    brand = Primary300,
)

val DarkSurfaceColors = SurfaceColors(
    primary = DarkSurfacePrimary,
    secondary = DarkSurfaceSecondary,
    tertiary = DarkSurfaceTertiary,
    elevated = DarkSurfaceElevated,
    inverse = Neutral100,
    brand = Primary600,
    brandSubtle = Color(0xFF2A9D7C).copy(alpha = 0.12f),
    rose = Color(0xFFF27A8E).copy(alpha = 0.10f),
    lavender = Color(0xFF9B8ADB).copy(alpha = 0.10f),
)

val DarkBorderColors = BorderColors(
    default = Color.White.copy(alpha = 0.10f),
    subtle = Color.White.copy(alpha = 0.06f),
    strong = Color.White.copy(alpha = 0.20f),
    brand = Primary600,
)

val DarkInteractiveColors = InteractiveColors(
    primary = Primary400,
    primaryHover = Primary300,
    primaryPressed = Primary200,
    primaryDisabled = Color(0xFF2A9D7C).copy(alpha = 0.20f),
    secondary = Neutral800,
    onPrimary = Neutral950,
)

val DarkOverlayColors = OverlayColors(
    light = Color(0xFF1E2522).copy(alpha = 0.90f),
    dark = Color.Black.copy(alpha = 0.6f),
    scrim = Color.Black.copy(alpha = 0.45f),
)

val DarkExtendedColors = ExtendedColors(
    skinMetric = DarkSkinMetricColors,
    functional = DarkFunctionalColors,
    camera = DefaultCameraColors,
    content = DarkContentColors,
    surface = DarkSurfaceColors,
    border = DarkBorderColors,
    interactive = DarkInteractiveColors,
    overlay = DarkOverlayColors,
)

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }
