package com.skintrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferencesEntity(
    @PrimaryKey val id: Int = 1,
    val onboardingCompleted: Boolean = false,
    val reminderEnabled: Boolean = false,
    val reminderTime: String = "08:00",
    val skinType: String? = null,
    val skinGoals: String? = null, // comma-separated: "acne,pore,brighten"
    val weeklyReportEnabled: Boolean = true,
    val aiNotificationEnabled: Boolean = false,
    val lastSyncTimestamp: String? = null,
)
