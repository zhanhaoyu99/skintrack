package com.skintrack.app.ui.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.skintrack.app.ui.screen.attribution.AttributionReportContent
import com.skintrack.app.ui.screen.camera.CameraScreen
import com.skintrack.app.ui.screen.dashboard.DashboardScreen
import com.skintrack.app.ui.screen.profile.ProfileScreen
import com.skintrack.app.ui.screen.timeline.TimelineScreen
import com.skintrack.app.ui.theme.Motion
import com.skintrack.app.ui.theme.gradients
import kotlinx.coroutines.delay

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val haptic = LocalHapticFeedback.current
        var selectedTab by remember { mutableStateOf(0) }
        var previousTab by remember { mutableStateOf(0) }
        var fabVisible by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            delay(Motion.MEDIUM.toLong())
            fabVisible = true
        }

        // 5-Tab: 首页 / 记录 / [拍照FAB] / 分析 / 我的
        val tabs = listOf(
            TabItem("首页", Icons.Filled.Home, Icons.Outlined.Home),
            TabItem("记录", Icons.AutoMirrored.Filled.List, Icons.AutoMirrored.Outlined.List),
            TabItem("拍照", Icons.Filled.Add, Icons.Filled.Add), // placeholder for FAB
            TabItem("分析", Icons.Filled.Star, Icons.Outlined.Star),
            TabItem("我的", Icons.Filled.Person, Icons.Outlined.Person),
        )

        val isDark = isSystemInDarkTheme()
        // Frosted glass nav bar background
        val navBarColor = if (isDark) {
            Color(0xFF1C1E20).copy(alpha = 0.88f)
        } else {
            Color.White.copy(alpha = 0.88f)
        }

        Scaffold(
            bottomBar = {
                Box {
                    // Top divider line for clean separation
                    HorizontalDivider(
                        thickness = 0.5.dp,
                        color = Color.Black.copy(alpha = 0.04f),
                    )

                    NavigationBar(
                        containerColor = navBarColor,
                        tonalElevation = 0.dp,
                    ) {
                        tabs.forEachIndexed { index, tab ->
                            if (index == 2) {
                                // Camera tab — empty spacer to reserve space for FAB
                                NavigationBarItem(
                                    selected = false,
                                    onClick = {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        navigator.push(CameraScreen())
                                    },
                                    icon = {
                                        // Invisible placeholder — FAB overlays this position
                                        Icon(
                                            imageVector = Icons.Filled.Add,
                                            contentDescription = "拍照",
                                            modifier = Modifier.size(0.dp),
                                        )
                                    },
                                    label = { Text("拍照") },
                                    enabled = true,
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color.Transparent,
                                    ),
                                )
                            } else {
                                // Map tab index to content index (skip camera)
                                val contentIndex = when (index) {
                                    0 -> 0
                                    1 -> 1
                                    3 -> 2
                                    4 -> 3
                                    else -> 0
                                }
                                val isSelected = selectedTab == contentIndex
                                NavigationBarItem(
                                    selected = isSelected,
                                    onClick = {
                                        previousTab = selectedTab
                                        selectedTab = contentIndex
                                    },
                                    icon = {
                                        Icon(
                                            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                            contentDescription = tab.title,
                                        )
                                    },
                                    label = {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                        ) {
                                            Text(tab.title)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            // Active indicator dot
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .then(
                                                        if (isSelected) {
                                                            Modifier.background(
                                                                color = MaterialTheme.colorScheme.primary,
                                                                shape = CircleShape,
                                                            )
                                                        } else {
                                                            Modifier
                                                        }
                                                    ),
                                            )
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        indicatorColor = Color.Transparent,
                                        selectedIconColor = MaterialTheme.colorScheme.primary,
                                        unselectedIconColor = Color(0xFF9CA3AF),
                                        selectedTextColor = MaterialTheme.colorScheme.primary,
                                        unselectedTextColor = Color(0xFF9CA3AF),
                                    ),
                                )
                            }
                        }
                    }

                    // Elevated FAB with gradient + glow + highlight border
                    AnimatedVisibility(
                        visible = fabVisible,
                        enter = scaleIn() + fadeIn(),
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (-22).dp),
                    ) {
                        val fabGradient = MaterialTheme.gradients.fab
                        val brandGlow = Color(0xFF2D9F7F)
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .shadow(
                                    elevation = 16.dp,
                                    shape = CircleShape,
                                    ambientColor = brandGlow.copy(alpha = 0.45f),
                                    spotColor = brandGlow.copy(alpha = 0.45f),
                                )
                                .background(brush = fabGradient, shape = CircleShape)
                                .border(0.5.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                ) {
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    navigator.push(CameraScreen())
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "拍照记录",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp),
                            )
                        }
                    }
                }
            },
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        val goingRight = targetState > previousTab
                        val slideIn = slideInHorizontally { if (goingRight) it / 3 else -it / 3 } + fadeIn()
                        val slideOut = slideOutHorizontally { if (goingRight) -it / 3 else it / 3 } + fadeOut()
                        slideIn togetherWith slideOut
                    },
                ) { tab ->
                    when (tab) {
                        0 -> DashboardScreen(
                            onSwitchToTimeline = { previousTab = selectedTab; selectedTab = 1 },
                            onSwitchToProfile = { previousTab = selectedTab; selectedTab = 3 },
                        )
                        1 -> TimelineScreen()
                        2 -> AttributionReportContent()
                        3 -> ProfileScreen()
                    }
                }
            }
        }
    }
}

private data class TabItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
)
