package com.skintrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_subscription")
data class UserSubscriptionEntity(
    @PrimaryKey val userId: String,
    val plan: String,
    val startDate: Long,
    val expiryDate: Long,
    val isActive: Boolean,
)
