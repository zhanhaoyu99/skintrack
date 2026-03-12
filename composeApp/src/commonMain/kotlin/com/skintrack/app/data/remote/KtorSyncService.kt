package com.skintrack.app.data.remote

import com.skintrack.app.data.local.entity.DailyProductUsageEntity
import com.skintrack.app.data.local.entity.SkinRecordEntity
import com.skintrack.app.data.local.entity.SkincareProductEntity
import com.skintrack.app.data.remote.dto.ApiResponse
import com.skintrack.app.data.remote.dto.DailyProductUsageDto
import com.skintrack.app.data.remote.dto.ImageUploadResponse
import com.skintrack.app.data.remote.dto.SkinRecordDto
import com.skintrack.app.data.remote.dto.SkincareProductDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class KtorSyncService(
    private val client: HttpClient,
    private val baseUrl: String,
) : RemoteSyncService {

    // ── Skin Records ───────────────────────────────────
    override suspend fun uploadSkinRecords(entities: List<SkinRecordEntity>) {
        if (entities.isEmpty()) return
        val dtos = entities.map { it.toDto() }
        val response: ApiResponse<String> = client.post("$baseUrl/api/skin-records") {
            contentType(ContentType.Application.Json)
            setBody(dtos)
        }.body()
        if (!response.success) throw IllegalStateException(response.error)
    }

    override suspend fun loadSkinRecords(userId: String): List<SkinRecordEntity> {
        val response: ApiResponse<List<SkinRecordDto>> =
            client.get("$baseUrl/api/skin-records").body()
        if (!response.success) throw IllegalStateException(response.error)
        return response.data?.map { it.toEntity() } ?: emptyList()
    }

    // ── Skincare Products ──────────────────────────────
    override suspend fun uploadProducts(entities: List<SkincareProductEntity>, userId: String) {
        if (entities.isEmpty()) return
        val dtos = entities.map { it.toDto(userId) }
        val response: ApiResponse<String> = client.post("$baseUrl/api/products") {
            contentType(ContentType.Application.Json)
            setBody(dtos)
        }.body()
        if (!response.success) throw IllegalStateException(response.error)
    }

    override suspend fun loadProducts(userId: String): List<SkincareProductEntity> {
        val response: ApiResponse<List<SkincareProductDto>> =
            client.get("$baseUrl/api/products").body()
        if (!response.success) throw IllegalStateException(response.error)
        return response.data?.map { it.toEntity() } ?: emptyList()
    }

    // ── Daily Product Usage ────────────────────────────
    override suspend fun uploadUsage(entities: List<DailyProductUsageEntity>) {
        if (entities.isEmpty()) return
        val dtos = entities.map { it.toDto() }
        val response: ApiResponse<String> = client.post("$baseUrl/api/usage") {
            contentType(ContentType.Application.Json)
            setBody(dtos)
        }.body()
        if (!response.success) throw IllegalStateException(response.error)
    }

    // ── Image Upload ───────────────────────────────────
    override suspend fun uploadImage(userId: String, fileName: String, imageBytes: ByteArray): String {
        val response: ApiResponse<ImageUploadResponse> =
            client.submitFormWithBinaryData(
                url = "$baseUrl/api/images/upload",
                formData = formData {
                    append("file", imageBytes, Headers.build {
                        append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                        append(HttpHeaders.ContentType, "image/jpeg")
                    })
                },
            ).body()
        if (!response.success) throw IllegalStateException(response.error)
        return response.data!!.imageUrl
    }
}

// ── Entity ↔ DTO Mapping (same as SupabaseSyncService) ──

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
