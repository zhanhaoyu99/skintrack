package com.skintrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_product_usage")
data class DailyProductUsageEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val productId: String,
    val usedDate: Long,
    val synced: Boolean = false,
)
