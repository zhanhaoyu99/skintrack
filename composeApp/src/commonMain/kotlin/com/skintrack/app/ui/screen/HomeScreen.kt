package com.skintrack.app.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import cafe.adriel.voyager.core.screen.Screen
import com.skintrack.app.ui.screen.camera.CameraScreen
import com.skintrack.app.ui.screen.profile.ProfileScreen
import com.skintrack.app.ui.screen.timeline.TimelineScreen

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        var selectedTab by remember { mutableStateOf(0) }

        val tabs = listOf(
            TabItem("拍照", Icons.Filled.Home, Icons.Outlined.Home),
            TabItem("趋势", Icons.Filled.Search, Icons.Outlined.Search),
            TabItem("我的", Icons.Filled.Person, Icons.Outlined.Person),
        )

        Scaffold(
            bottomBar = {
                NavigationBar {
                    tabs.forEachIndexed { index, tab ->
                        NavigationBarItem(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            icon = {
                                Icon(
                                    imageVector = if (selectedTab == index) tab.selectedIcon else tab.unselectedIcon,
                                    contentDescription = tab.title
                                )
                            },
                            label = { Text(tab.title) }
                        )
                    }
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                when (selectedTab) {
                    0 -> CameraTabContent()
                    1 -> TimelineTabContent()
                    2 -> ProfileTabContent()
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

@Composable
private fun CameraTabContent() {
    CameraScreen()
}

@Composable
private fun TimelineTabContent() {
    TimelineScreen()
}

@Composable
private fun ProfileTabContent() {
    ProfileScreen()
}
