package com.skintrack.server.database.tables

import org.jetbrains.exposed.sql.Table

object DailyProductUsageTable : Table("daily_product_usage") {
    val id = varchar("id", 36)
    val userId = varchar("user_id", 36).references(UsersTable.id)
    val productId = varchar("product_id", 36).references(SkincareProductsTable.id)
    val usedDate = varchar("used_date", 20)
    val createdAt = varchar("created_at", 50).nullable()

    override val primaryKey = PrimaryKey(id)
}
