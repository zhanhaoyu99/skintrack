package com.skintrack.app.ui.screen.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skintrack.app.data.local.dao.UserPreferencesDao
import com.skintrack.app.data.local.entity.UserPreferencesEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val userPreferencesDao: UserPreferencesDao,
) : ViewModel() {

    private val _isOnboardingCompleted = MutableStateFlow<Boolean?>(null)
    val isOnboardingCompleted: StateFlow<Boolean?> = _isOnboardingCompleted.asStateFlow()

    init {
        viewModelScope.launch {
            val prefs = userPreferencesDao.getPreferences()
            _isOnboardingCompleted.value = prefs?.onboardingCompleted ?: false
        }
    }

    fun completeOnboarding(skinType: String?) {
        viewModelScope.launch {
            val existing = userPreferencesDao.getPreferences()
            if (existing != null) {
                userPreferencesDao.setOnboardingCompleted(true)
                skinType?.let { userPreferencesDao.setSkinType(it) }
            } else {
                userPreferencesDao.savePreferences(
                    UserPreferencesEntity(
                        onboardingCompleted = true,
                        skinType = skinType,
                    )
                )
            }
            _isOnboardingCompleted.value = true
        }
    }
}
