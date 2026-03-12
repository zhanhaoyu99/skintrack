package com.skintrack.app.data.remote

import com.skintrack.app.data.local.entity.DailyProductUsageEntity
import com.skintrack.app.data.local.entity.SkinRecordEntity
import com.skintrack.app.data.local.entity.SkincareProductEntity
import com.skintrack.app.data.remote.dto.DailyProductUsageDto
import com.skintrack.app.data.remote.dto.SkinRecordDto
import com.skintrack.app.data.remote.dto.SkincareProductDto
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class SupabaseSyncService(
    private val supabase: SupabaseClient,
) {
    private val postgrest get() = supabase.postgrest

    // ── Skin Records ───────────────────────────────────
    suspend fun uploadSkinRecords(entities: List<SkinRecordEntity>) {
        if (entities.isEmpty()) return
        val dtos = entities.map { it.toDto() }
        postgrest.from("skin_records").upsert(dtos)
    }

    suspend fun loadSkinRecords(userId: String): List<SkinRecordEntity> {
        val dtos = postgrest.from("skin_records")
            .select { filter { eq("user_id", userId) } }
            .decodeList<SkinRecordDto>()
        return dtos.map { it.toEntity() }
    }

    // ── Skincare Products ──────────────────────────────
    suspend fun uploadProducts(entities: List<SkincareProductEntity>, userId: String) {
        if (entities.isEmpty()) return
        val dtos = entities.map { it.toDto(userId) }
        postgrest.from("skincare_products").upsert(dtos)
    }

    suspend fun loadProducts(userId: String): List<SkincareProductEntity> {
        val dtos = postgrest.from("skincare_products")
            .select { filter { eq("user_id", userId) } }
            .decodeList<SkincareProductDto>()
        return dtos.map { it.toEntity() }
    }

    // ── Daily Product Usage ────────────────────────────
    suspend fun uploadUsage(entities: List<DailyProductUsageEntity>) {
        if (entities.isEmpty()) return
        val dtos = entities.map { it.toDto() }
        postgrest.from("daily_product_usage").upsert(dtos)
    }

    // ── Image Upload ───────────────────────────────────
    suspend fun uploadImage(userId: String, fileName: String, imageBytes: ByteArray): String {
        val path = "$userId/$fileName"
        supabase.storage.from("skin-photos").upload(path, imageBytes) {
            upsert = true
        }
        return supabase.storage.from("skin-photos").publicUrl(path)
    }
}

// ── Entity ↔ DTO Mapping ──────────────────────────────

private fun SkinRecordEntity.toDto() = SkinRecordDto(
    id = id,
    userId = userId,
    skinType = skinType,
    overallScore = overallScore,
    acneCount = acneCount,
    poreScore = poreScore,
    rednessScore = rednessScore,
    evenScore = evenScore,
    blackheadDensity = blackheadDensity,
    notes = notes,
    imageUrl = imageUrl,
    analysisJson = analysisJson,
    recordedAt = Instant.fromEpochMilliseconds(recordedAt).toString(),
)

private fun SkinRecordDto.toEntity() = SkinRecordEntity(
    id = id,
    userId = userId,
    skinType = skinType,
    overallScore = overallScore,
    acneCount = acneCount,
    poreScore = poreScore,
    rednessScore = rednessScore,
    evenScore = evenScore,
    blackheadDensity = blackheadDensity,
    notes = notes,
    imageUrl = imageUrl,
    analysisJson = analysisJson,
    recordedAt = Instant.parse(recordedAt).toEpochMilliseconds(),
    createdAt = createdAt?.let { Instant.parse(it).toEpochMilliseconds() }
        ?: Instant.parse(recordedAt).toEpochMilliseconds(),
    synced = true,
)

private fun SkincareProductEntity.toDto(userId: String) = SkincareProductDto(
    id = id,
    userId = userId,
    name = name,
    brand = brand,
    category = category,
    imageUrl = imageUrl,
    barcode = barcode,
)

private fun SkincareProductDto.toEntity() = SkincareProductEntity(
    id = id,
    userId = userId,
    name = name,
    brand = brand,
    category = category,
    imageUrl = imageUrl,
    barcode = barcode,
    synced = true,
)

private fun DailyProductUsageEntity.toDto() = DailyProductUsageDto(
    id = id,
    userId = userId,
    productId = productId,
    usedDate = Instant.fromEpochMilliseconds(usedDate)
        .toLocalDateTime(TimeZone.currentSystemDefault()).date.toString(),
)
