package com.skintrack.server.service

import com.skintrack.server.database.tables.CheckInStreaksTable
import com.skintrack.server.database.tables.UserSubscriptionsTable
import com.skintrack.server.dto.CheckInStreakDto
import com.skintrack.server.dto.UserSubscriptionDto
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.upsert

class SubscriptionService {

    fun loadSubscription(userId: String): UserSubscriptionDto? = transaction {
        UserSubscriptionsTable.selectAll()
            .where { UserSubscriptionsTable.userId eq userId }
            .firstOrNull()?.let { row ->
                UserSubscriptionDto(
                    userId = row[UserSubscriptionsTable.userId],
                    plan = row[UserSubscriptionsTable.plan],
                    startDate = row[UserSubscriptionsTable.startDate],
                    expiryDate = row[UserSubscriptionsTable.expiryDate],
                    isActive = row[UserSubscriptionsTable.isActive],
                )
            }
    }

    fun updateSubscription(dto: UserSubscriptionDto) = transaction {
        UserSubscriptionsTable.upsert {
            it[userId] = dto.userId
            it[plan] = dto.plan
            it[startDate] = dto.startDate
            it[expiryDate] = dto.expiryDate
            it[isActive] = dto.isActive
        }
    }

    fun loadStreak(userId: String): CheckInStreakDto? = transaction {
        CheckInStreaksTable.selectAll()
            .where { CheckInStreaksTable.userId eq userId }
            .firstOrNull()?.let { row ->
                CheckInStreakDto(
                    userId = row[CheckInStreaksTable.userId],
                    currentStreak = row[CheckInStreaksTable.currentStreak],
                    longestStreak = row[CheckInStreaksTable.longestStreak],
                    lastCheckInDate = row[CheckInStreaksTable.lastCheckInDate],
                )
            }
    }

    fun updateStreak(dto: CheckInStreakDto) = transaction {
        CheckInStreaksTable.upsert {
            it[userId] = dto.userId
            it[currentStreak] = dto.currentStreak
            it[longestStreak] = dto.longestStreak
            it[lastCheckInDate] = dto.lastCheckInDate
        }
    }
}
