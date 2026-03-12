package com.skintrack.app.ui.screen.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skintrack.app.data.remote.SyncManager
import com.skintrack.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val syncManager: SyncManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun toggleMode() {
        _uiState.update {
            it.copy(
                isLoginMode = !it.isLoginMode,
                errorMessage = null,
            )
        }
    }

    fun submit(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.isSubmitting) return

        _uiState.update { it.copy(isSubmitting = true, errorMessage = null) }

        viewModelScope.launch {
            val result = if (state.isLoginMode) {
                authRepository.login(state.email, state.password)
            } else {
                authRepository.register(state.email, state.password)
            }

            result.fold(
                onSuccess = {
                    _uiState.update { it.copy(isSubmitting = false) }
                    syncManager.syncAll()
                    onSuccess()
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isSubmitting = false,
                            errorMessage = error.message,
                        )
                    }
                },
            )
        }
    }
}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoginMode: Boolean = true,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
)
