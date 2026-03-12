package com.skintrack.app.domain.model

enum class FeatureGate {
    UNLIMITED_RECORDS,
    DETAILED_AI_REPORT,
    ATTRIBUTION_REPORT,
    SHARE_CARD,
}

val FeatureGate.lockedMessage: String
    get() = when (this) {
        FeatureGate.UNLIMITED_RECORDS -> "免费版最多保存 3 条记录，升级会员解锁无限记录"
        FeatureGate.DETAILED_AI_REPORT -> "AI 详细分析报告为会员专属功能"
        FeatureGate.ATTRIBUTION_REPORT -> "归因分析报告为会员专属功能"
        FeatureGate.SHARE_CARD -> "分享对比卡片为会员专属功能"
    }
