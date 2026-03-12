package com.skintrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "check_in_streak")
data class CheckInStreakEntity(
    @PrimaryKey val userId: String,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastCheckInDate: String?,
)
