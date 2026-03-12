package com.skintrack.app.platform

import com.skintrack.app.domain.model.SubscriptionPlan
import kotlinx.coroutines.delay

actual class PaymentManager {
    actual suspend fun purchase(plan: SubscriptionPlan): PaymentResult {
        // Mock: simulate payment processing
        delay(1_000)
        println("PaymentManager: Mock purchase success for $plan")
        return PaymentResult.Success
    }

    actual suspend fun restorePurchase(): PaymentResult {
        delay(1_000)
        println("PaymentManager: Mock restore purchase")
        return PaymentResult.Success
    }
}
