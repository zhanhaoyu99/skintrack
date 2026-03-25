package com.skintrack.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * 1:1 mapping to CSS §8 gradient tokens.
 */
@Immutable
data class GradientColors(
    val primary: Brush,         // grad-primary: 135° primary-500 → 400 → 300
    val primaryBold: Brush,     // grad-primary-bold: 145° primary-700 → 500 → 400
    val hero: Brush,            // grad-hero: 145° primary-800 → 600 → 500 → 300
    val button: Brush,          // grad-button: 180° primary-400 → 500
    val warm: Brush,            // grad-warm: 135° #FFF8F2 → primary-50
    val surface: Brush,         // grad-surface: 180° neutral-v-50 → neutral-0
    val scoreRing: Brush,       // grad-score: 135° primary → teal → info
    val glass: Brush,           // grad-glass: 135° white 92% → 72%
    val roseWarm: Brush,        // grad-rose-warm
    val lavenderSoft: Brush,    // grad-lavender-soft
    val triWarm: Brush,         // grad-tri-warm: mint → rose → lavender
    val skin: Brush,            // grad-skin
    val profile: Brush,         // grad-profile: 160° primary-600 → 500 → 300
    val paywall: Brush,         // grad-paywall: 160° primary-700 → 500 → secondary-400
    val vipBadge: Brush,        // gold gradient
    val fab: Brush,             // same as primaryBold for FAB
    val chipActive: Brush,      // active chip gradient
)

val LightGradientColors = GradientColors(
    primary = Brush.linearGradient(listOf(Primary500, Primary400, Primary300)),
    primaryBold = Brush.linearGradient(listOf(Primary700, Primary500, Primary400)),
    hero = Brush.linearGradient(listOf(Primary800, Primary600, Primary500, Primary300)),
    button = Brush.verticalGradient(listOf(Primary400, Primary500)),
    warm = Brush.linearGradient(listOf(Color(0xFFFFF8F2), Primary50)),
    surface = Brush.verticalGradient(listOf(NeutralV50, Neutral0)),
    scoreRing = Brush.sweepGradient(
        listOf(Primary500, Primary300, MetricPoreLight, Info400)
    ),
    glass = Brush.linearGradient(
        listOf(Color.White.copy(alpha = 0.92f), Color.White.copy(alpha = 0.72f))
    ),
    roseWarm = Brush.linearGradient(listOf(Rose50, Color(0xFFFFE8EC), Color(0xFFFFF5F3))),
    lavenderSoft = Brush.linearGradient(listOf(Lavender50, Lavender100, Color(0xFFF8F5FF))),
    triWarm = Brush.linearGradient(listOf(Primary50, Rose50, Lavender50)),
    skin = Brush.linearGradient(
        listOf(
            Color(0xFFE8CFC0), Color(0xFFD4B8A8), Color(0xFFC8A898),
            Color(0xFFDCC4B4), Color(0xFFE8D0C0)
        )
    ),
    profile = Brush.linearGradient(listOf(Primary600, Primary500, Primary300)),
    paywall = Brush.linearGradient(listOf(Primary700, Primary500, Secondary400)),
    vipBadge = Brush.linearGradient(listOf(GoldLight, GoldDark)),
    fab = Brush.linearGradient(listOf(Primary700, Primary500, Primary400)),
    chipActive = Brush.linearGradient(listOf(Primary500, Primary400)),
)

val DarkGradientColors = GradientColors(
    primary = Brush.linearGradient(listOf(Primary400, Primary500, Primary600)),
    primaryBold = Brush.linearGradient(listOf(Primary300, Primary500, Primary700)),
    hero = Brush.linearGradient(
        listOf(Color(0xFF0A2E24), Primary800, Primary600, Primary500)
    ),
    button = Brush.verticalGradient(listOf(Primary400, Primary600)),
    warm = Brush.linearGradient(listOf(Color(0xFF1A1714), Color(0xFF0F1A16))),
    surface = Brush.verticalGradient(listOf(DarkSurfacePrimary, Color(0xFF131917))),
    scoreRing = Brush.sweepGradient(
        listOf(Primary400, Primary300, MetricPoreDark, Info400)
    ),
    glass = Brush.linearGradient(
        listOf(
            Color(0xFF1E2522).copy(alpha = 0.92f),
            Color(0xFF1E2522).copy(alpha = 0.72f)
        )
    ),
    roseWarm = Brush.linearGradient(
        listOf(
            Color(0xFFF27A8E).copy(alpha = 0.06f),
            Color(0xFFF27A8E).copy(alpha = 0.03f)
        )
    ),
    lavenderSoft = Brush.linearGradient(
        listOf(
            Color(0xFF9B8ADB).copy(alpha = 0.06f),
            Color(0xFF9B8ADB).copy(alpha = 0.03f)
        )
    ),
    triWarm = Brush.linearGradient(
        listOf(
            Color(0xFF2A9D7C).copy(alpha = 0.06f),
            Color(0xFFF27A8E).copy(alpha = 0.04f),
            Color(0xFF9B8ADB).copy(alpha = 0.04f)
        )
    ),
    skin = Brush.linearGradient(
        listOf(
            Color(0xFF5A4A40), Color(0xFF4A3C34), Color(0xFF403530),
            Color(0xFF4D403A), Color(0xFF5A4A40)
        )
    ),
    profile = Brush.linearGradient(listOf(Primary800, Primary700, Primary600)),
    paywall = Brush.linearGradient(
        listOf(Color(0xFF0A2E24), Primary700, Secondary700)
    ),
    vipBadge = Brush.linearGradient(listOf(GoldLight, GoldDark)),
    fab = Brush.linearGradient(listOf(Primary300, Primary500, Primary700)),
    chipActive = Brush.linearGradient(listOf(Primary400, Primary300)),
)

val LocalGradientColors = staticCompositionLocalOf { LightGradientColors }
