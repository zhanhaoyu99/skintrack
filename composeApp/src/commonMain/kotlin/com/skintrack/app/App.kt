package com.skintrack.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.skintrack.app.domain.repository.AuthRepository
import com.skintrack.app.ui.screen.HomeScreen
import com.skintrack.app.ui.screen.auth.AuthScreen
import com.skintrack.app.ui.theme.SkinTrackTheme
import org.koin.compose.koinInject

@Composable
fun App() {
    SkinTrackTheme {
        val authRepository: AuthRepository = koinInject()
        var startScreen by remember { mutableStateOf<Screen?>(null) }

        LaunchedEffect(Unit) {
            val user = authRepository.currentUser()
            startScreen = if (user != null) HomeScreen() else AuthScreen()
        }

        startScreen?.let { screen ->
            Navigator(screen) { navigator ->
                SlideTransition(navigator)
            }
        }
    }
}
