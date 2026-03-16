package com.skintrack.app.data.remote

import com.skintrack.app.data.local.dao.SyncQueueDao
import com.skintrack.app.data.local.dao.UserPreferencesDao
import com.skintrack.app.data.local.entity.SyncQueueEntity
import com.skintrack.app.domain.repository.AuthRepository
import com.skintrack.app.domain.repository.ProductRepository
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.domain.repository.SkinRecordRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.datetime.Clock

sealed interface SyncState {
    data object Idle : SyncState
    data object Syncing : SyncState
    data object Success : SyncState
    data class Error(val message: String) : SyncState
}

class SyncManager(
    private val authRepository: AuthRepository,
    private val skinRecordRepository: SkinRecordRepository,
    private val productRepository: ProductRepository,
    private val syncQueueDao: SyncQueueDao,
    private val userPreferencesDao: UserPreferencesDao,
    private val networkMonitor: NetworkMonitor,
    private val aiAnalysisService: AiAnalysisService? = null,
    private val imageStorage: com.skintrack.app.platform.ImageStorage? = null,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val syncMutex = Mutex()

    private val _syncState = MutableStateFlow<SyncState>(SyncState.Idle)
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    /** Last successful sync timestamp (ISO-8601 string), persisted in UserPreferences. */
    private var lastSyncTimestamp: String? = null

    init {
        // Auto-sync when network becomes available
        scope.launch {
            networkMonitor.isOnline.collect { online ->
                if (online && hasPendingOperations()) {
                    syncAll()
                }
            }
        }
    }

    /**
     * Incremental sync: push local unsynced -> pull remote changes since last sync.
     * Called on login success and app start (if authenticated).
     */
    fun syncAll() {
        scope.launch {
            if (!networkMonitor.isOnline.value) return@launch
            if (!syncMutex.tryLock()) return@launch // Prevent concurrent sync
            val userId = authRepository.currentUser()?.userId
            if (userId == null) { syncMutex.unlock(); return@launch }
            _syncState.value = SyncState.Syncing
            try {
                // Push local changes first (from sync queue)
                processSyncQueue()

                // Push remaining unsynced records
                skinRecordRepository.syncToRemote()
                productRepository.syncToRemote()

                // Pull remote data incrementally (only changes since last sync)
                if (lastSyncTimestamp == null) {
                    lastSyncTimestamp = userPreferencesDao.getLastSyncTimestamp()
                }
                val since = lastSyncTimestamp
                skinRecordRepository.pullFromRemote(userId, since)
                productRepository.pullFromRemote(userId, since)

                // Retry pending AI analysis
                retryPendingAnalysis(userId)

                // Persist last sync timestamp
                val now = Clock.System.now().toString()
                lastSyncTimestamp = now
                userPreferencesDao.setLastSyncTimestamp(now)
                _syncState.value = SyncState.Success
            } catch (e: Exception) {
                _syncState.value = SyncState.Error(e.message ?: "Sync failed")
            } finally {
                syncMutex.unlock()
            }
        }
    }

    /**
     * Push-only sync: upload unsynced local data.
     * Called after saving new records/products.
     */
    fun pushChanges() {
        scope.launch {
            if (!networkMonitor.isOnline.value) return@launch
            try {
                processSyncQueue()
                skinRecordRepository.syncToRemote()
                productRepository.syncToRemote()
            } catch (_: Exception) {
                // Non-fatal
            }
        }
    }

    /**
     * Enqueue a sync operation for persistence.
     * Should be called when creating/updating/deleting entities.
     */
    suspend fun enqueueOperation(
        entityType: String,
        entityId: String,
        action: String,
    ) {
        syncQueueDao.insertOperation(
            SyncQueueEntity(
                entityType = entityType,
                entityId = entityId,
                action = action,
                createdAt = Clock.System.now().toEpochMilliseconds(),
            )
        )
        // Try to push immediately if online
        if (networkMonitor.isOnline.value) {
            pushChanges()
        }
    }

    /**
     * Retry AI analysis for records that were saved without analysis results.
     * Uses exponential backoff per record: 1s → 2s → 4s, max 3 attempts.
     */
    private suspend fun retryPendingAnalysis(userId: String) {
        val service = aiAnalysisService ?: return
        val pendingRecords = skinRecordRepository.getPendingAnalysis(userId)
        if (pendingRecords.isEmpty()) return

        for (record in pendingRecords) {
            // Skip records without image data (we can't re-analyze without the image on server)
            // In practice, the image should be available via localImagePath or imageUrl
            val imageBytes = loadImageForRecord(record) ?: continue

            var delayMs = 1000L
            var success = false
            for (attempt in 1..3) {
                try {
                    val result = service.analyzeSkinImage(imageBytes)
                    val updated = record.copy(
                        overallScore = result.overallScore,
                        acneCount = result.acneCount,
                        poreScore = result.poreScore,
                        rednessScore = result.rednessScore,
                        evenScore = result.evenScore,
                        blackheadDensity = result.blackheadDensity,
                        skinType = result.skinType,
                    )
                    skinRecordRepository.save(updated)
                    success = true
                    break
                } catch (_: Exception) {
                    if (attempt < 3) {
                        delay(delayMs)
                        delayMs *= 2
                    }
                }
            }
            if (!success) {
                // Move on to next record; this one will be retried on next sync
                continue
            }
        }
    }

    /**
     * Load image bytes for a record from local storage.
     * Returns null if the image is not available locally.
     */
    private suspend fun loadImageForRecord(record: SkinRecord): ByteArray? {
        val storage = imageStorage ?: return null
        val path = record.localImagePath ?: return null
        return try {
            storage.loadImage(path)
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Process all pending operations from the sync queue.
     */
    private suspend fun processSyncQueue() {
        val pending = syncQueueDao.getPendingOperations()
        if (pending.isEmpty()) return

        for (operation in pending) {
            try {
                when (operation.entityType) {
                    ENTITY_SKIN_RECORD -> skinRecordRepository.syncToRemote()
                    ENTITY_PRODUCT -> productRepository.syncToRemote()
                    ENTITY_USAGE -> productRepository.syncToRemote()
                }
                // Remove from queue after successful sync
                syncQueueDao.deleteOperation(operation.id)
            } catch (_: Exception) {
                // Keep in queue for retry
                break
            }
        }
    }

    private suspend fun hasPendingOperations(): Boolean =
        syncQueueDao.getPendingOperations().isNotEmpty()

    companion object {
        const val ENTITY_SKIN_RECORD = "SKIN_RECORD"
        const val ENTITY_PRODUCT = "PRODUCT"
        const val ENTITY_USAGE = "USAGE"

        const val ACTION_CREATE = "CREATE"
        const val ACTION_UPDATE = "UPDATE"
        const val ACTION_DELETE = "DELETE"
    }
}
