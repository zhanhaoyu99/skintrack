package com.skintrack.app.data.repository

import com.skintrack.app.data.local.dao.AuthSessionDao
import com.skintrack.app.data.local.dao.CheckInStreakDao
import com.skintrack.app.data.local.dao.SkinRecordDao
import com.skintrack.app.data.local.dao.UserSubscriptionDao
import com.skintrack.app.data.local.entity.CheckInStreakEntity
import com.skintrack.app.data.local.entity.UserSubscriptionEntity
import com.skintrack.app.domain.model.CheckInStreak
import com.skintrack.app.domain.model.SubscriptionPlan
import com.skintrack.app.domain.model.UserSubscription
import com.skintrack.app.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

private const val TRIAL_DAYS = 14

class SubscriptionRepositoryImpl(
    private val userSubscriptionDao: UserSubscriptionDao,
    private val checkInStreakDao: CheckInStreakDao,
    private val authSessionDao: AuthSessionDao,
    private val skinRecordDao: SkinRecordDao,
) : SubscriptionRepository {

    override fun observeSubscription(userId: String): Flow<UserSubscription?> =
        userSubscriptionDao.observe(userId).map { it?.toDomain() }

    override suspend fun getSubscription(userId: String): UserSubscription? =
        userSubscriptionDao.get(userId)?.toDomain()

    override suspend fun saveSubscription(subscription: UserSubscription) {
        userSubscriptionDao.save(subscription.toEntity())
    }

    override suspend fun isPremium(userId: String): Boolean {
        if (isInTrialPeriod(userId)) return true
        val subscription = userSubscriptionDao.get(userId) ?: return false
        return subscription.isActive &&
            Clock.System.now().toEpochMilliseconds() < subscription.expiryDate
    }

    override suspend fun isInTrialPeriod(userId: String): Boolean {
        val session = authSessionDao.getSession() ?: return false
        val createdAt = Instant.fromEpochMilliseconds(session.createdAt)
        val now = Clock.System.now()
        val daysSinceCreation = (now - createdAt).inWholeDays
        return daysSinceCreation < TRIAL_DAYS
    }

    override suspend fun getRecordCount(userId: String): Int =
        skinRecordDao.countByUser(userId)

    override fun observeStreak(userId: String): Flow<CheckInStreak?> =
        checkInStreakDao.observe(userId).map { it?.toDomain() }

    override suspend fun getStreak(userId: String): CheckInStreak? =
        checkInStreakDao.get(userId)?.toDomain()

    override suspend fun saveStreak(streak: CheckInStreak) {
        checkInStreakDao.save(streak.toEntity())
    }
}

private fun UserSubscriptionEntity.toDomain() = UserSubscription(
    userId = userId,
    plan = runCatching { SubscriptionPlan.valueOf(plan) }.getOrDefault(SubscriptionPlan.FREE),
    startDate = Instant.fromEpochMilliseconds(startDate),
    expiryDate = Instant.fromEpochMilliseconds(expiryDate),
    isActive = isActive,
)

private fun UserSubscription.toEntity() = UserSubscriptionEntity(
    userId = userId,
    plan = plan.name,
    startDate = startDate.toEpochMilliseconds(),
    expiryDate = expiryDate.toEpochMilliseconds(),
    isActive = isActive,
)

private fun CheckInStreakEntity.toDomain() = CheckInStreak(
    userId = userId,
    currentStreak = currentStreak,
    longestStreak = longestStreak,
    lastCheckInDate = lastCheckInDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
)

private fun CheckInStreak.toEntity() = CheckInStreakEntity(
    userId = userId,
    currentStreak = currentStreak,
    longestStreak = longestStreak,
    lastCheckInDate = lastCheckInDate?.toString(),
)
