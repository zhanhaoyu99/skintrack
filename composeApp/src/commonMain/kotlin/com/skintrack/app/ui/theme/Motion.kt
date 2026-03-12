package com.skintrack.app.ui.theme

import androidx.compose.animation.core.CubicBezierEasing

object Motion {
    const val SHORT = 150
    const val MEDIUM = 300
    const val LONG = 500

    val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    val Standard = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)
}
