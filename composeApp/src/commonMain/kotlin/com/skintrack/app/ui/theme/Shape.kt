package com.skintrack.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * 1:1 mapping to CSS §6 --radius-* tokens.
 *
 * | CSS Token    | M3 Shape   | Value |
 * |--------------|------------|-------|
 * | radius-xs    | extraSmall | 4dp   |
 * | radius-sm    | small      | 8dp   |
 * | radius-md    | medium     | 12dp  |
 * | radius-lg    | large      | 16dp  |
 * | radius-xl    | extraLarge | 24dp  |
 */
val SkinTrackShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // radius-xs
    small = RoundedCornerShape(8.dp),        // radius-sm
    medium = RoundedCornerShape(12.dp),      // radius-md
    large = RoundedCornerShape(16.dp),       // radius-lg
    extraLarge = RoundedCornerShape(24.dp),  // radius-xl
)

// Additional shapes not in M3 Shapes
val ShapeXxl = RoundedCornerShape(32.dp)       // radius-xxl — phone frame, hero cards
val FullRoundedShape = RoundedCornerShape(percent = 50) // radius-full — pills, avatars
