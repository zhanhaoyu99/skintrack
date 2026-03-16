package com.skintrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "skincare_products")
data class SkincareProductEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val name: String,
    val brand: String? = null,
    val category: String,
    val imageUrl: String? = null,
    val barcode: String? = null,
    val synced: Boolean = false,
    val usagePeriod: String = "both",
    val updatedAt: Long = kotlinx.datetime.Clock.System.now().toEpochMilliseconds(),
)
