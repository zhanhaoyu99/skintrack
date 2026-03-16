package com.skintrack.server.service

import com.skintrack.server.database.tables.SkinRecordsTable
import com.skintrack.server.dto.SkinRecordDto
import kotlinx.datetime.Clock
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.upsert

class SkinRecordService {

    fun loadByUser(userId: String, since: String? = null): List<SkinRecordDto> = transaction {
        SkinRecordsTable.selectAll()
            .where {
                if (since != null) {
                    (SkinRecordsTable.userId eq userId) and
                        (SkinRecordsTable.updatedAt.isNotNull()) and
                        (SkinRecordsTable.updatedAt greater since)
                } else {
                    SkinRecordsTable.userId eq userId
                }
            }
            .map { row ->
                SkinRecordDto(
                    id = row[SkinRecordsTable.id],
                    userId = row[SkinRecordsTable.userId],
                    skinType = row[SkinRecordsTable.skinType],
                    overallScore = row[SkinRecordsTable.overallScore],
                    acneCount = row[SkinRecordsTable.acneCount],
                    poreScore = row[SkinRecordsTable.poreScore],
                    rednessScore = row[SkinRecordsTable.rednessScore],
                    evenScore = row[SkinRecordsTable.evenScore],
                    blackheadDensity = row[SkinRecordsTable.blackheadDensity],
                    notes = row[SkinRecordsTable.notes],
                    imageUrl = row[SkinRecordsTable.imageUrl],
                    analysisJson = row[SkinRecordsTable.analysisJson],
                    recordedAt = row[SkinRecordsTable.recordedAt],
                    createdAt = row[SkinRecordsTable.createdAt],
                    updatedAt = row[SkinRecordsTable.updatedAt],
                )
            }
    }

    fun upsert(dtos: List<SkinRecordDto>, userId: String) = transaction {
        val now = Clock.System.now().toString()
        dtos.forEach { dto ->
            SkinRecordsTable.upsert {
                it[id] = dto.id
                it[SkinRecordsTable.userId] = userId // Force JWT userId
                it[skinType] = dto.skinType
                it[overallScore] = dto.overallScore
                it[acneCount] = dto.acneCount
                it[poreScore] = dto.poreScore
                it[rednessScore] = dto.rednessScore
                it[evenScore] = dto.evenScore
                it[blackheadDensity] = dto.blackheadDensity
                it[notes] = dto.notes
                it[imageUrl] = dto.imageUrl
                it[analysisJson] = dto.analysisJson
                it[recordedAt] = dto.recordedAt
                it[createdAt] = dto.createdAt
                it[updatedAt] = now
            }
        }
    }
}
