package com.skintrack.app.data.remote

import com.skintrack.app.domain.repository.AuthRepository
import com.skintrack.app.domain.repository.ProductRepository
import com.skintrack.app.domain.repository.SkinRecordRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class SyncStatus {
    IDLE, SYNCING, SUCCESS, ERROR
}

class SyncManager(
    private val authRepository: AuthRepository,
    private val skinRecordRepository: SkinRecordRepository,
    private val productRepository: ProductRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _status = MutableStateFlow(SyncStatus.IDLE)
    val status: StateFlow<SyncStatus> = _status

    /**
     * Full sync: push local unsynced → pull remote.
     * Called on login success and app start (if authenticated).
     */
    fun syncAll() {
        scope.launch {
            val userId = authRepository.currentUser()?.userId ?: return@launch
            _status.value = SyncStatus.SYNCING
            try {
                // Push local changes first
                skinRecordRepository.syncToRemote()
                productRepository.syncToRemote()

                // Then pull remote data
                skinRecordRepository.pullFromRemote(userId)
                productRepository.pullFromRemote(userId)

                _status.value = SyncStatus.SUCCESS
            } catch (_: Exception) {
                _status.value = SyncStatus.ERROR
            }
        }
    }

    /**
     * Push-only sync: upload unsynced local data.
     * Called after saving new records/products.
     */
    fun pushChanges() {
        scope.launch {
            try {
                skinRecordRepository.syncToRemote()
                productRepository.syncToRemote()
            } catch (_: Exception) {
                // Non-fatal
            }
        }
    }
}
