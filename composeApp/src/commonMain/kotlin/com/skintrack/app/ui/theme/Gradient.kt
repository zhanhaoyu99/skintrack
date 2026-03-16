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
    val hero: Brush,
    val vipBadge: Brush,
)

val LightGradientColors = GradientColors(
    primary = Brush.linearGradient(listOf(Mint400, Mint300)),
    scoreRing = Brush.sweepGradient(listOf(Mint400, Apricot300, Mint400)),
    warm = Brush.linearGradient(listOf(Apricot50, Mint50)),
    surface = Brush.verticalGradient(listOf(BackgroundLight, Color.White)),
    hero = Brush.linearGradient(
        listOf(Color(0xFF1A7A63), Color(0xFF2D9F7F), Color(0xFF3DBFA0), Color(0xFF58CAA5)),
    ),
    vipBadge = Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA500))),
)

val DarkGradientColors = GradientColors(
    primary = Brush.linearGradient(listOf(Mint400, Mint300)),
    scoreRing = Brush.sweepGradient(listOf(Mint400, Apricot300, Mint400)),
    warm = Brush.linearGradient(listOf(Apricot500, Mint600)),
    surface = Brush.verticalGradient(listOf(BackgroundDark, SurfaceDark)),
    hero = Brush.linearGradient(
        listOf(Color(0xFF155146), Color(0xFF1D6B5B), Color(0xFF248068), Color(0xFF2D9F7F)),
    ),
    vipBadge = Brush.linearGradient(listOf(Color(0xFFFFD700), Color(0xFFFFA500))),
)

val LocalGradientColors = staticCompositionLocalOf { LightGradientColors }
