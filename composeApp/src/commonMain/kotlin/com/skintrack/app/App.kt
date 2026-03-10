package com.skintrack.app

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.skintrack.app.ui.screen.HomeScreen
import com.skintrack.app.ui.theme.SkinTrackTheme

@Composable
fun App() {
    SkinTrackTheme {
        Navigator(HomeScreen()) { navigator ->
            SlideTransition(navigator)
        }
    }
}
