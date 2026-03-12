package com.skintrack.app.domain.model

enum class SubscriptionPlan {
    FREE,
    MONTHLY,
    YEARLY,
}

val SubscriptionPlan.displayName: String
    get() = when (this) {
        SubscriptionPlan.FREE -> "免费版"
        SubscriptionPlan.MONTHLY -> "月度会员"
        SubscriptionPlan.YEARLY -> "年度会员"
    }

val SubscriptionPlan.price: String
    get() = when (this) {
        SubscriptionPlan.FREE -> "免费"
        SubscriptionPlan.MONTHLY -> "¥19.9/月"
        SubscriptionPlan.YEARLY -> "¥168/年"
    }
