package com.skintrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val entityType: String, // SKIN_RECORD, PRODUCT, USAGE
    val entityId: String,
    val action: String, // CREATE, UPDATE, DELETE
    val createdAt: Long,
)
