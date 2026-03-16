package com.skintrack.app.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skintrack.app.data.local.dao.UserPreferencesDao
import com.skintrack.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditProfileViewModel(
    private val authRepository: AuthRepository,
    private val userPreferencesDao: UserPreferencesDao,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val user = authRepository.currentUser()
            val prefs = userPreferencesDao.getPreferences()

            _uiState.update {
                it.copy(
                    displayName = user?.displayName.orEmpty(),
                    email = user?.email.orEmpty(),
                    skinType = prefs?.skinType,
                    skinGoals = prefs?.skinGoals
                        ?.split(",")
                        ?.filter { goal -> goal.isNotBlank() }
                        ?.toSet()
                        ?: emptySet(),
                    isLoading = false,
                )
            }
        }
    }

    fun updateDisplayName(name: String) {
        _uiState.update { it.copy(displayName = name, error = null, isSaved = false) }
    }

    fun updateSkinType(skinType: String) {
        _uiState.update { it.copy(skinType = skinType, error = null, isSaved = false) }
    }

    fun toggleSkinGoal(goalId: String) {
        _uiState.update { state ->
            val goals = state.skinGoals.toMutableSet()
            if (goals.contains(goalId)) goals.remove(goalId) else goals.add(goalId)
            state.copy(skinGoals = goals, isSaved = false)
        }
    }

    fun save() {
        val state = _uiState.value
        if (state.isLoading) return

        val name = state.displayName.trim()
        if (name.isBlank()) {
            _uiState.update { it.copy(error = "昵称不能为空") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            // Update display name
            val nameResult = authRepository.updateDisplayName(name)
            if (nameResult.isFailure) {
                _uiState.update {
                    it.copy(isLoading = false, error = nameResult.exceptionOrNull()?.message)
                }
                return@launch
            }

            // Update skin type
            val skinType = state.skinType
            if (skinType != null) {
                userPreferencesDao.setSkinType(skinType)
            }

            // Update skin goals
            val goalsStr = state.skinGoals.joinToString(",").ifBlank { null }
            userPreferencesDao.setSkinGoals(goalsStr)

            _uiState.update { it.copy(isLoading = false, isSaved = true) }
        }
    }
}

data class EditProfileUiState(
    val displayName: String = "",
    val email: String = "",
    val skinType: String? = null,
    val skinGoals: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false,
)
