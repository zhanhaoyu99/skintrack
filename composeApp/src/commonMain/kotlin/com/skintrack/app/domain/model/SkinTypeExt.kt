package com.skintrack.app.domain.model

val SkinType.displayName: String
    get() = when (this) {
        SkinType.OILY -> "油性"
        SkinType.DRY -> "干性"
        SkinType.COMBINATION -> "混合"
        SkinType.SENSITIVE -> "敏感"
        SkinType.NORMAL -> "中性"
        SkinType.UNKNOWN -> "未知"
    }
