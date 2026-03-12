package com.skintrack.server.database.tables

import org.jetbrains.exposed.sql.Table

object UserSubscriptionsTable : Table("user_subscriptions") {
    val userId = varchar("user_id", 36).references(UsersTable.id)
    val plan = varchar("plan", 50)
    val startDate = varchar("start_date", 20)
    val expiryDate = varchar("expiry_date", 20)
    val isActive = bool("is_active").default(true)

    override val primaryKey = PrimaryKey(userId)
}
