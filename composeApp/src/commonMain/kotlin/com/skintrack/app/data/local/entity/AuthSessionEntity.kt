package com.skintrack.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "auth_session")
data class AuthSessionEntity(
    @PrimaryKey val id: Int = 1,
    val userId: String,
    val email: String,
    val displayName: String? = null,
    val createdAt: Long,
)
