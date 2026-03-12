package com.skintrack.app.domain.usecase

import com.skintrack.app.domain.model.FeatureGate
import com.skintrack.app.domain.repository.AuthRepository
import com.skintrack.app.domain.repository.SubscriptionRepository

private const val FREE_RECORD_LIMIT = 3

class CheckFeatureAccess(
    private val subscriptionRepository: SubscriptionRepository,
    private val authRepository: AuthRepository,
) {
    private suspend fun resolveUserId(): String =
        authRepository.currentUser()?.userId ?: "local-user"

    suspend fun canAccess(feature: FeatureGate): Boolean {
        val userId = resolveUserId()
        if (subscriptionRepository.isPremium(userId)) return true
        return when (feature) {
            FeatureGate.UNLIMITED_RECORDS -> {
                subscriptionRepository.getRecordCount(userId) < FREE_RECORD_LIMIT
            }
            FeatureGate.DETAILED_AI_REPORT,
            FeatureGate.ATTRIBUTION_REPORT,
            FeatureGate.SHARE_CARD -> false
        }
    }

    suspend fun isPremium(): Boolean {
        val userId = resolveUserId()
        return subscriptionRepository.isPremium(userId)
    }
}
