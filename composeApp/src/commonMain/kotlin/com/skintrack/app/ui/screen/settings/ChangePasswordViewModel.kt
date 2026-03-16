package com.skintrack.app.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skintrack.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState

    fun updateOldPassword(password: String) {
        _uiState.update { it.copy(oldPassword = password, error = null) }
    }

    fun updateNewPassword(password: String) {
        _uiState.update { it.copy(newPassword = password, error = null) }
    }

    fun updateConfirmPassword(password: String) {
        _uiState.update { it.copy(confirmPassword = password, error = null) }
    }

    fun changePassword() {
        val state = _uiState.value
        if (state.isLoading) return

        // Validation
        when {
            state.oldPassword.isBlank() -> {
                _uiState.update { it.copy(error = "请输入当前密码") }
                return
            }
            state.newPassword.length < 6 -> {
                _uiState.update { it.copy(error = "新密码至少需要6个字符") }
                return
            }
            state.confirmPassword != state.newPassword -> {
                _uiState.update { it.copy(error = "两次输入的密码不一致") }
                return
            }
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            authRepository.changePassword(state.oldPassword, state.newPassword).fold(
                onSuccess = {
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                },
            )
        }
    }
}

data class ChangePasswordUiState(
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
)
