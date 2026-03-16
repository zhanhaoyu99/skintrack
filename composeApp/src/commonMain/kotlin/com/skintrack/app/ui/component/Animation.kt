package com.skintrack.app.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import com.skintrack.app.ui.theme.Motion
import kotlin.math.min

fun Modifier.animateListItem(index: Int): Modifier = composed {
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(24f) }

    LaunchedEffect(Unit) {
        val delay = min(index * 50, 1500)
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
        val delay = min(index * 50, 1500)
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

fun Modifier.animateCardEntrance(index: Int): Modifier = composed {
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.95f) }

    LaunchedEffect(Unit) {
        val delay = min(index * 80, 2000)
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
        val delay = min(index * 80, 2000)
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = Motion.MEDIUM,
                delayMillis = delay,
                easing = Motion.EmphasizedDecelerate,
            ),
        )
    }

    this.graphicsLayer {
        this.alpha = alpha.value
        scaleX = scale.value
        scaleY = scale.value
    }
}

fun Modifier.animateFadeIn(delayMillis: Int = 0): Modifier = composed {
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = Motion.MEDIUM,
                delayMillis = delayMillis,
                easing = Motion.EmphasizedDecelerate,
            ),
        )
    }

    this.graphicsLayer {
        this.alpha = alpha.value
    }
}

fun Modifier.animatePulse(): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.05f,
        animationSpec = InfiniteRepeatableSpec(
            animation = tween(
                durationMillis = Motion.LONG,
                easing = Motion.Standard,
            ),
            repeatMode = RepeatMode.Reverse,
        ),
    )

    this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}
