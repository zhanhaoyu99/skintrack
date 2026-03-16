package com.skintrack.server.database.tables

import org.jetbrains.exposed.sql.Table

object SkincareProductsTable : Table("skincare_products") {
    val id = varchar("id", 36)
    val userId = varchar("user_id", 36).references(UsersTable.id)
    val name = varchar("name", 255)
    val brand = varchar("brand", 255).nullable()
    val category = varchar("category", 50)
    val imageUrl = text("image_url").nullable()
    val barcode = varchar("barcode", 100).nullable()
    val createdAt = varchar("created_at", 50).nullable()
    val updatedAt = varchar("updated_at", 50).nullable()

    override val primaryKey = PrimaryKey(id)
}
