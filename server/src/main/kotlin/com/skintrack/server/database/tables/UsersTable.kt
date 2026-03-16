package com.skintrack.server.database.tables

import org.jetbrains.exposed.sql.Table

object UsersTable : Table("users") {
    val id = varchar("id", 36)
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val displayName = varchar("display_name", 255).nullable()
    val skinType = varchar("skin_type", 50).nullable()
    val avatarUrl = text("avatar_url").nullable()
    val refreshToken = text("refresh_token").nullable()
    val resetCode = varchar("reset_code", 6).nullable()
    val resetCodeExpiresAt = long("reset_code_expires_at").nullable()
    val fcmToken = text("fcm_token").nullable()
    val devicePlatform = varchar("device_platform", 20).nullable()
    val createdAt = varchar("created_at", 50)

    override val primaryKey = PrimaryKey(id)
}
