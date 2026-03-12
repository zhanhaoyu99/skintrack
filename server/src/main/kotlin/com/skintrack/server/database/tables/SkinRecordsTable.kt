package com.skintrack.server.database.tables

import org.jetbrains.exposed.sql.Table

object SkinRecordsTable : Table("skin_records") {
    val id = varchar("id", 36)
    val userId = varchar("user_id", 36).references(UsersTable.id)
    val skinType = varchar("skin_type", 50).default("UNKNOWN")
    val overallScore = integer("overall_score").nullable()
    val acneCount = integer("acne_count").nullable()
    val poreScore = integer("pore_score").nullable()
    val rednessScore = integer("redness_score").nullable()
    val evenScore = integer("even_score").nullable()
    val blackheadDensity = integer("blackhead_density").nullable()
    val notes = text("notes").nullable()
    val imageUrl = text("image_url").nullable()
    val analysisJson = text("analysis_json").nullable()
    val recordedAt = varchar("recorded_at", 50)
    val createdAt = varchar("created_at", 50).nullable()

    override val primaryKey = PrimaryKey(id)
}
