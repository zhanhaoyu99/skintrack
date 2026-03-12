package com.skintrack.app.domain.usecase

import com.skintrack.app.domain.model.CheckInStreak
import com.skintrack.app.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlin.math.max

class UpdateCheckInStreak(
    private val subscriptionRepository: SubscriptionRepository,
) {
    suspend fun onNewRecord(userId: String = "local-user"): String? {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val existing = subscriptionRepository.getStreak(userId)

        val (newCurrent, newLongest) = when {
            existing == null -> 1 to 1
            existing.lastCheckInDate == today -> return null // Already checked in today
            existing.lastCheckInDate == today.minus(DatePeriod(days = 1)) -> {
                val current = existing.currentStreak + 1
                current to max(current, existing.longestStreak)
            }
            else -> 1 to max(1, existing?.longestStreak ?: 1)
        }

        val streak = CheckInStreak(
            userId = userId,
            currentStreak = newCurrent,
            longestStreak = newLongest,
            lastCheckInDate = today,
        )
        subscriptionRepository.saveStreak(streak)

        return getMilestoneMessage(newCurrent)
    }

    fun observeStreak(userId: String = "local-user"): Flow<CheckInStreak?> =
        subscriptionRepository.observeStreak(userId)

    private fun getMilestoneMessage(streak: Int): String? = when (streak) {
        7 -> "连续打卡 7 天！坚持得很好"
        14 -> "连续打卡 14 天！两周坚持太棒了"
        30 -> "连续打卡 30 天！一个月的坚持，皮肤一定有变化"
        else -> null
    }
}
