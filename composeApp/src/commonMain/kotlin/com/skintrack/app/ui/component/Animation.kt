package com.skintrack.app.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import com.skintrack.app.ui.theme.Motion

fun Modifier.animateListItem(index: Int): Modifier = composed {
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(24f) }

    LaunchedEffect(Unit) {
        val delay = index * 50
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = Motion.MEDIUM,
                delayMillis = delay,
                easing = Motion.EmphasizedDecelerate,
            ),
        )
    }
    LaunchedEffect(Unit) {
        val delay = index * 50
        offsetY.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = Motion.MEDIUM,
                delayMillis = delay,
                easing = Motion.EmphasizedDecelerate,
            ),
        )
    }

    this.graphicsLayer {
        this.alpha = alpha.value
        translationY = offsetY.value
    }
}
