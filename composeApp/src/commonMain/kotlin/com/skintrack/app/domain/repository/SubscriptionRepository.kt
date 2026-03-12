package com.skintrack.app.domain.repository

import com.skintrack.app.domain.model.CheckInStreak
import com.skintrack.app.domain.model.SubscriptionPlan
import com.skintrack.app.domain.model.UserSubscription
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    fun observeSubscription(userId: String): Flow<UserSubscription?>
    suspend fun getSubscription(userId: String): UserSubscription?
    suspend fun saveSubscription(subscription: UserSubscription)
    suspend fun isPremium(userId: String): Boolean
    suspend fun isInTrialPeriod(userId: String): Boolean
    suspend fun getRecordCount(userId: String): Int

    fun observeStreak(userId: String): Flow<CheckInStreak?>
    suspend fun getStreak(userId: String): CheckInStreak?
    suspend fun saveStreak(streak: CheckInStreak)
}
