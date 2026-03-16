package com.skintrack.app.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.spring

object Motion {
    const val SHORT = 150
    const val MEDIUM = 300
    const val LONG = 500
    const val EXTRA_LONG = 800

    val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1.0f)
    val EmphasizedAccelerate = CubicBezierEasing(0.3f, 0.0f, 0.8f, 0.15f)
    val Standard = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

    val BouncySpring = spring<Float>(dampingRatio = 0.6f, stiffness = 400f)
    val GentleSpring = spring<Float>(dampingRatio = 0.8f, stiffness = 300f)
}
