package com.skintrack.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Immutable
data class GradientColors(
    val primary: Brush,
    val scoreRing: Brush,
    val warm: Brush,
    val surface: Brush,
)

val LightGradientColors = GradientColors(
    primary = Brush.linearGradient(listOf(Mint400, Mint300)),
    scoreRing = Brush.sweepGradient(listOf(Mint400, Apricot300, Mint400)),
    warm = Brush.linearGradient(listOf(Apricot50, Mint50)),
    surface = Brush.verticalGradient(listOf(BackgroundLight, Color.White)),
)

val DarkGradientColors = GradientColors(
    primary = Brush.linearGradient(listOf(Mint400, Mint300)),
    scoreRing = Brush.sweepGradient(listOf(Mint400, Apricot300, Mint400)),
    warm = Brush.linearGradient(listOf(Apricot500, Mint600)),
    surface = Brush.verticalGradient(listOf(BackgroundDark, SurfaceDark)),
)

val LocalGradientColors = staticCompositionLocalOf { LightGradientColors }
