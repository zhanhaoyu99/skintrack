package com.skintrack.app.data.remote

import com.skintrack.app.data.local.entity.DailyProductUsageEntity
import com.skintrack.app.data.local.entity.SkinRecordEntity
import com.skintrack.app.data.local.entity.SkincareProductEntity

/**
 * Backend-agnostic interface for remote data sync.
 * Implementations: SupabaseSyncService (current), KtorSyncService (future).
 */
interface RemoteSyncService {

    // ── Skin Records ───────────────────────────────────
    suspend fun uploadSkinRecords(entities: List<SkinRecordEntity>)
    suspend fun loadSkinRecords(userId: String): List<SkinRecordEntity>

    // ── Skincare Products ──────────────────────────────
    suspend fun uploadProducts(entities: List<SkincareProductEntity>, userId: String)
    suspend fun loadProducts(userId: String): List<SkincareProductEntity>

    // ── Daily Product Usage ────────────────────────────
    suspend fun uploadUsage(entities: List<DailyProductUsageEntity>)

    // ── Image Upload ───────────────────────────────────
    /**
     * Upload a skin photo and return the public URL.
     */
    suspend fun uploadImage(userId: String, fileName: String, imageBytes: ByteArray): String
}
