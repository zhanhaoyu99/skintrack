package com.skintrack.app.platform

import com.skintrack.app.domain.model.SubscriptionPlan

expect class PaymentManager() {
    suspend fun purchase(plan: SubscriptionPlan): PaymentResult
    suspend fun restorePurchase(): PaymentResult
}

sealed interface PaymentResult {
    data object Success : PaymentResult
    data class Error(val message: String) : PaymentResult
    data object Cancelled : PaymentResult
}
