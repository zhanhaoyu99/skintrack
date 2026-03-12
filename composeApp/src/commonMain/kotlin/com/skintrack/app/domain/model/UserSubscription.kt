package com.skintrack.app.domain.model

import kotlinx.datetime.Instant

data class UserSubscription(
    val userId: String,
    val plan: SubscriptionPlan,
    val startDate: Instant,
    val expiryDate: Instant,
    val isActive: Boolean,
)
