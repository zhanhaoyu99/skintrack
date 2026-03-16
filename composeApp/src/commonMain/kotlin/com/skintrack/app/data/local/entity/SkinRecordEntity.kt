package com.skintrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "skin_records",
    indices = [Index(value = ["userId", "recordedAt"])],
)
data class SkinRecordEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val skinType: String,
    val overallScore: Int? = null,
    val acneCount: Int? = null,
    val poreScore: Int? = null,
    val rednessScore: Int? = null,
    val evenScore: Int? = null,
    val blackheadDensity: Int? = null,
    val notes: String? = null,
    val imageUrl: String? = null,
    val localImagePath: String? = null,
    val analysisJson: String? = null,
    val recordedAt: Long,
    val createdAt: Long,
    val updatedAt: Long = createdAt,
    val synced: Boolean = false,
)
