package com.skintrack.app.data.repository

import com.skintrack.app.data.local.dao.SkinRecordDao
import com.skintrack.app.data.local.entity.SkinRecordEntity
import com.skintrack.app.domain.model.SkinRecord
import com.skintrack.app.domain.model.SkinType
import com.skintrack.app.domain.repository.SkinRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

class SkinRecordRepositoryImpl(
    private val skinRecordDao: SkinRecordDao,
) : SkinRecordRepository {

    override fun getRecordsByUser(userId: String): Flow<List<SkinRecord>> =
        skinRecordDao.getRecordsByUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getRecordsBetween(userId: String, start: Instant, end: Instant): Flow<List<SkinRecord>> =
        skinRecordDao.getRecordsBetween(
            userId,
            start.toEpochMilliseconds(),
            end.toEpochMilliseconds(),
        ).map { entities -> entities.map { it.toDomain() } }

    override suspend fun getById(id: String): SkinRecord? =
        skinRecordDao.getById(id)?.toDomain()

    override suspend fun save(record: SkinRecord) {
        skinRecordDao.insert(record.toEntity())
    }

    override suspend fun delete(record: SkinRecord) {
        skinRecordDao.delete(record.toEntity())
    }

    override suspend fun syncToRemote() {
        // TODO: Get unsynced records and push to Supabase
        val unsynced = skinRecordDao.getUnsynced()
        // Upload each to Supabase, then mark synced
    }
}

private fun SkinRecordEntity.toDomain() = SkinRecord(
    id = id,
    userId = userId,
    skinType = runCatching { SkinType.valueOf(skinType) }.getOrDefault(SkinType.UNKNOWN),
    overallScore = overallScore,
    acneCount = acneCount,
    poreScore = poreScore,
    rednessScore = rednessScore,
    evenScore = evenScore,
    blackheadDensity = blackheadDensity,
    notes = notes,
    imageUrl = imageUrl,
    localImagePath = localImagePath,
    analysisJson = analysisJson,
    recordedAt = Instant.fromEpochMilliseconds(recordedAt),
    createdAt = Instant.fromEpochMilliseconds(createdAt),
    synced = synced,
)

private fun SkinRecord.toEntity() = SkinRecordEntity(
    id = id,
    userId = userId,
    skinType = skinType.name,
    overallScore = overallScore,
    acneCount = acneCount,
    poreScore = poreScore,
    rednessScore = rednessScore,
    evenScore = evenScore,
    blackheadDensity = blackheadDensity,
    notes = notes,
    imageUrl = imageUrl,
    localImagePath = localImagePath,
    analysisJson = analysisJson,
    recordedAt = recordedAt.toEpochMilliseconds(),
    createdAt = createdAt.toEpochMilliseconds(),
    synced = synced,
)
