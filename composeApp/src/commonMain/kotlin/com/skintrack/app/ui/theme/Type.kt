package com.skintrack.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * 1:1 mapping to CSS §4 typography tokens.
 * Custom named tokens beyond M3 Typography.
 */
@Immutable
data class AppTypography(
    // Headings
    val h1: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,       // 700
        fontSize = 30.sp,
        lineHeight = 36.sp,                 // 30 × 1.20
        letterSpacing = (-0.5).sp,
    ),
    val h2: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,       // 700
        fontSize = 24.sp,
        lineHeight = 30.sp,                 // 24 × 1.25
        letterSpacing = (-0.4).sp,
    ),
    val h3: TextStyle = TextStyle(
        fontWeight = FontWeight.SemiBold,   // 600
        fontSize = 19.sp,
        lineHeight = 24.7.sp,              // 19 × 1.30
        letterSpacing = (-0.2).sp,
    ),
    val h4: TextStyle = TextStyle(
        fontWeight = FontWeight.SemiBold,   // 600
        fontSize = 16.sp,
        lineHeight = 21.6.sp,              // 16 × 1.35
        letterSpacing = (-0.1).sp,
    ),

    // Body
    val b1: TextStyle = TextStyle(
        fontWeight = FontWeight.Normal,     // 400
        fontSize = 16.sp,
        lineHeight = 24.sp,                 // 16 × 1.50
        letterSpacing = 0.sp,
    ),
    val b2: TextStyle = TextStyle(
        fontWeight = FontWeight.Normal,     // 400
        fontSize = 14.sp,
        lineHeight = 20.3.sp,              // 14 × 1.45
        letterSpacing = 0.05.sp,
    ),
    val b3: TextStyle = TextStyle(
        fontWeight = FontWeight.Normal,     // 400
        fontSize = 13.sp,
        lineHeight = 18.2.sp,              // 13 × 1.40
        letterSpacing = 0.05.sp,
    ),

    // Caption / Label
    val c1: TextStyle = TextStyle(
        fontWeight = FontWeight.Medium,     // 500
        fontSize = 12.sp,
        lineHeight = 16.2.sp,              // 12 × 1.35
        letterSpacing = 0.2.sp,
    ),
    val c2: TextStyle = TextStyle(
        fontWeight = FontWeight.SemiBold,   // 600
        fontSize = 10.sp,
        lineHeight = 13.sp,                // 10 × 1.30
        letterSpacing = 0.3.sp,
    ),

    // Button
    val btn: TextStyle = TextStyle(
        fontWeight = FontWeight.SemiBold,   // 600
        fontSize = 16.sp,
        lineHeight = 16.sp,                 // 1.0
        letterSpacing = (-0.1).sp,
    ),
    val btnSm: TextStyle = TextStyle(
        fontWeight = FontWeight.SemiBold,   // 600
        fontSize = 14.sp,
        lineHeight = 14.sp,                 // 1.0
        letterSpacing = 0.sp,
    ),

    // Numeric Display
    val numXl: TextStyle = TextStyle(
        fontWeight = FontWeight.ExtraBold,  // 800
        fontSize = 38.sp,
        lineHeight = 41.8.sp,              // 38 × 1.10
        letterSpacing = (-1).sp,
    ),
    val numLg: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,       // 700
        fontSize = 26.sp,
        lineHeight = 29.9.sp,              // 26 × 1.15
        letterSpacing = (-0.5).sp,
    ),
    val numMd: TextStyle = TextStyle(
        fontWeight = FontWeight.SemiBold,   // 600
        fontSize = 18.sp,
        lineHeight = 21.6.sp,              // 18 × 1.20
        letterSpacing = (-0.2).sp,
    ),
    val numSm: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,       // 700
        fontSize = 14.sp,
        lineHeight = 16.8.sp,              // 14 × 1.20
        letterSpacing = 0.sp,
    ),

    // Section header title (CSS §18: 17px / 700 / -0.3px)
    val sectionTitle: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        lineHeight = 22.sp,
        letterSpacing = (-0.3).sp,
    ),
)

val LocalAppTypography = staticCompositionLocalOf { AppTypography() }

/**
 * M3 Typography mapped from CSS design tokens.
 * Maps CSS tokens to closest M3 slots for Material components.
 */
val SkinTrackTypography = Typography(
    // h1 → headlineSmall (closest M3 size range for 30sp page titles)
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.5).sp,
    ),
    // h2 → headlineSmall
    headlineSmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        lineHeight = 30.sp,
        letterSpacing = (-0.4).sp,
    ),
    // h3 → titleLarge
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 19.sp,
        lineHeight = 24.7.sp,
        letterSpacing = (-0.2).sp,
    ),
    // h4 → titleMedium
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 21.6.sp,
        letterSpacing = (-0.1).sp,
    ),
    // b1 → bodyLarge
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
    ),
    // b2 → bodyMedium
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.3.sp,
        letterSpacing = 0.05.sp,
    ),
    // b3 → bodySmall
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.2.sp,
        letterSpacing = 0.05.sp,
    ),
    // c1 → labelMedium
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.2.sp,
        letterSpacing = 0.2.sp,
    ),
    // c2 → labelSmall
    labelSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        lineHeight = 13.sp,
        letterSpacing = 0.3.sp,
    ),
    // btn → labelLarge
    labelLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 16.sp,
        letterSpacing = (-0.1).sp,
    ),
    // titleSmall used for h4-like secondary titles
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.3.sp,
        letterSpacing = 0.05.sp,
    ),
)
