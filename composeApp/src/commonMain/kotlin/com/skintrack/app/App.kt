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
import com.skintrack.app.data.local.dao.UserPreferencesDao
import com.skintrack.app.data.remote.SyncManager
import com.skintrack.app.domain.repository.AuthRepository
import com.skintrack.app.ui.screen.HomeScreen
import com.skintrack.app.ui.screen.auth.AuthScreen
import com.skintrack.app.ui.screen.onboarding.OnboardingScreen
import com.skintrack.app.ui.theme.SkinTrackTheme
import org.koin.compose.koinInject

@Composable
fun App() {
    SkinTrackTheme {
        val authRepository: AuthRepository = koinInject()
        val syncManager: SyncManager = koinInject()
        val userPreferencesDao: UserPreferencesDao = koinInject()
        var startScreen by remember { mutableStateOf<Screen?>(null) }

        LaunchedEffect(Unit) {
            val prefs = userPreferencesDao.getPreferences()
            val onboardingCompleted = prefs?.onboardingCompleted ?: false

            when {
                !onboardingCompleted -> {
                    startScreen = OnboardingScreen()
                }
                else -> {
                    val user = authRepository.currentUser()
                    if (user != null) {
                        syncManager.syncAll()
                        startScreen = HomeScreen()
                    } else {
                        startScreen = AuthScreen()
                    }
                }
            }
        }

        startScreen?.let { screen ->
            Navigator(screen) { navigator ->
                SlideTransition(navigator)
            }
        }
    }
}
