package com.skintrack.app.domain.repository

import com.skintrack.app.domain.model.SkinRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface SkinRecordRepository {
    fun getRecordsByUser(userId: String): Flow<List<SkinRecord>>
    fun getRecordsBetween(userId: String, start: Instant, end: Instant): Flow<List<SkinRecord>>
    suspend fun getById(id: String): SkinRecord?
    suspend fun save(record: SkinRecord)
    suspend fun delete(record: SkinRecord)
    suspend fun syncToRemote()
    suspend fun pullFromRemote(userId: String)
}
