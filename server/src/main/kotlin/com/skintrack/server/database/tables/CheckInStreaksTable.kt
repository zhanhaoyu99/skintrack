package com.skintrack.server.database.tables

import org.jetbrains.exposed.sql.Table

object CheckInStreaksTable : Table("check_in_streaks") {
    val userId = varchar("user_id", 36).references(UsersTable.id)
    val currentStreak = integer("current_streak").default(0)
    val longestStreak = integer("longest_streak").default(0)
    val lastCheckInDate = varchar("last_check_in_date", 20).nullable()

    override val primaryKey = PrimaryKey(userId)
}
