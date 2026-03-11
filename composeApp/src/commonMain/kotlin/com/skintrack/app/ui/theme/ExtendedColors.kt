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
    val warning: Color,
    val error: Color,
    val info: Color,
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
data class ExtendedColors(
    val skinMetric: SkinMetricColors,
    val functional: FunctionalColors,
    val camera: CameraColors,
)

val LightSkinMetricColors = SkinMetricColors(
    acne = SkinAcneLight,
    pore = SkinPoreLight,
    evenness = SkinEvennessLight,
    hydration = SkinHydrationLight,
    redness = SkinRednessLight,
)

val DarkSkinMetricColors = SkinMetricColors(
    acne = SkinAcneDark,
    pore = SkinPoreDark,
    evenness = SkinEvennessDark,
    hydration = SkinHydrationDark,
    redness = SkinRednessDark,
)

val LightFunctionalColors = FunctionalColors(
    success = SuccessLight,
    warning = WarningLight,
    error = ErrorLight,
    info = InfoLight,
)

val DarkFunctionalColors = FunctionalColors(
    success = SuccessDark,
    warning = WarningDark,
    error = ErrorDark,
    info = InfoDark,
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
)

val DarkExtendedColors = ExtendedColors(
    skinMetric = DarkSkinMetricColors,
    functional = DarkFunctionalColors,
    camera = DefaultCameraColors,
)

val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }
