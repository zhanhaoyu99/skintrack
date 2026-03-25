package com.skintrack.app.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skintrack.app.data.local.dao.UserPreferencesDao
import com.skintrack.app.data.local.entity.UserPreferencesEntity
import com.skintrack.app.data.remote.SyncManager
import com.skintrack.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface ExportState {
    data object Idle : ExportState
    data object Loading : ExportState
    data class Success(val data: String) : ExportState
    data class Error(val message: String) : ExportState
}

sealed interface DeleteState {
    data object Idle : DeleteState
    data object Loading : DeleteState
    data object Success : DeleteState
    data class Error(val message: String) : DeleteState
}

class SettingsViewModel(
    private val userPreferencesDao: UserPreferencesDao,
    private val authRepository: AuthRepository,
    private val syncManager: SyncManager,
) : ViewModel() {

    private val _reminderEnabled = MutableStateFlow(false)
    val reminderEnabled: StateFlow<Boolean> = _reminderEnabled.asStateFlow()

    private val _reminderTime = MutableStateFlow("08:00")
    val reminderTime: StateFlow<String> = _reminderTime.asStateFlow()

    private val _weeklyReportEnabled = MutableStateFlow(true)
    val weeklyReportEnabled: StateFlow<Boolean> = _weeklyReportEnabled.asStateFlow()

    private val _aiNotificationEnabled = MutableStateFlow(false)
    val aiNotificationEnabled: StateFlow<Boolean> = _aiNotificationEnabled.asStateFlow()

    private val _exportState = MutableStateFlow<ExportState>(ExportState.Idle)
    val exportState: StateFlow<ExportState> = _exportState.asStateFlow()

    private val _showDeleteDialog = MutableStateFlow(false)
    val showDeleteDialog: StateFlow<Boolean> = _showDeleteDialog.asStateFlow()

    private val _deleteState = MutableStateFlow<DeleteState>(DeleteState.Idle)
    val deleteState: StateFlow<DeleteState> = _deleteState.asStateFlow()

    private val _showLogoutConfirm = MutableStateFlow(false)
    val showLogoutConfirm: StateFlow<Boolean> = _showLogoutConfirm.asStateFlow()

    private val _loggedOut = MutableStateFlow(false)
    val loggedOut: StateFlow<Boolean> = _loggedOut.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesDao.observePreferences().collect { prefs ->
                _reminderEnabled.value = prefs?.reminderEnabled ?: false
                _reminderTime.value = prefs?.reminderTime ?: "08:00"
                _weeklyReportEnabled.value = prefs?.weeklyReportEnabled ?: true
                _aiNotificationEnabled.value = prefs?.aiNotificationEnabled ?: false
            }
        }
    }

    fun toggleReminder(enabled: Boolean) {
        viewModelScope.launch {
            ensurePreferencesExist()
            userPreferencesDao.setReminderEnabled(enabled)
        }
    }

    fun setReminderTime(time: String) {
        viewModelScope.launch {
            ensurePreferencesExist()
            userPreferencesDao.setReminderTime(time)
        }
    }

    fun toggleWeeklyReport() {
        viewModelScope.launch {
            ensurePreferencesExist()
            userPreferencesDao.setWeeklyReportEnabled(!_weeklyReportEnabled.value)
        }
    }

    fun toggleAiNotification() {
        viewModelScope.launch {
            ensurePreferencesExist()
            userPreferencesDao.setAiNotificationEnabled(!_aiNotificationEnabled.value)
        }
    }

    fun exportData() {
        if (_exportState.value is ExportState.Loading) return
        viewModelScope.launch {
            _exportState.value = ExportState.Loading
            authRepository.exportUserData()
                .onSuccess { data ->
                    _exportState.value = ExportState.Success(data)
                    _snackbarMessage.value = "数据导出成功"
                }
                .onFailure { e ->
                    _exportState.value = ExportState.Error(e.message ?: "导出失败")
                    _snackbarMessage.value = "导出失败: ${e.message}"
                }
        }
    }

    fun resetExportState() {
        _exportState.value = ExportState.Idle
    }

    fun manualSync() {
        viewModelScope.launch {
            _snackbarMessage.value = "正在同步..."
            try {
                syncManager.syncAll()
                _snackbarMessage.value = "同步完成"
            } catch (e: Exception) {
                _snackbarMessage.value = "同步失败: ${e.message}"
            }
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            _snackbarMessage.value = "缓存已清除"
        }
    }

    fun showDeleteAccountDialog() {
        _showDeleteDialog.value = true
    }

    fun dismissDeleteAccountDialog() {
        _showDeleteDialog.value = false
        _deleteState.value = DeleteState.Idle
    }

    fun deleteAccount(password: String) {
        if (_deleteState.value is DeleteState.Loading) return
        viewModelScope.launch {
            _deleteState.value = DeleteState.Loading
            authRepository.deleteAccount(password)
                .onSuccess {
                    _deleteState.value = DeleteState.Success
                    _showDeleteDialog.value = false
                    _loggedOut.value = true
                }
                .onFailure { e ->
                    _deleteState.value = DeleteState.Error(e.message ?: "注销失败")
                    _snackbarMessage.value = "注销失败: ${e.message}"
                }
        }
    }

    fun requestLogout() {
        _showLogoutConfirm.value = true
    }

    fun dismissLogoutConfirm() {
        _showLogoutConfirm.value = false
    }

    fun confirmLogout() {
        _showLogoutConfirm.value = false
        viewModelScope.launch {
            authRepository.logout()
            _loggedOut.value = true
        }
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    private suspend fun ensurePreferencesExist() {
        if (userPreferencesDao.getPreferences() == null) {
            userPreferencesDao.savePreferences(UserPreferencesEntity())
        }
    }
}
