package com.skintrack.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 1:1 mapping to CSS §5 --space-* tokens.
 * Aliases: pageGutter = md (16dp), sectionGap = lg (24dp), cardPadding = md (16dp).
 */
@Immutable
data class Spacing(
    val xxs: Dp = 2.dp,     // space-2  — micro adjustment
    val xs: Dp = 4.dp,      // space-4  — tight spacing
    val iconGap: Dp = 6.dp, // space-6  — icon-text gap
    val sm: Dp = 8.dp,      // space-8  — inner element
    val compact: Dp = 10.dp,// space-10 — compact padding
    val listGap: Dp = 12.dp,// space-12 — list item gap
    val md: Dp = 16.dp,     // space-16 — standard / page gutter
    val cardInner: Dp = 20.dp, // space-20 — card internal
    val lg: Dp = 24.dp,     // space-24 — section gap
    val xl: Dp = 32.dp,     // space-32 — large gap
    val xxl: Dp = 40.dp,    // space-40 — extra large
    val section: Dp = 48.dp,// space-48 — page section
    val segment: Dp = 64.dp,// space-64 — page segment
) {
    // Semantic aliases
    val pageGutter: Dp get() = md
    val sectionGap: Dp get() = lg
    val cardPadding: Dp get() = md
}

val LocalSpacing = staticCompositionLocalOf { Spacing() }
