package com.skintrack.app.domain.model

import kotlinx.datetime.LocalDate

data class DailyProductUsage(
    val id: String,
    val userId: String,
    val productId: String,
    val usedDate: LocalDate,
    val synced: Boolean = false,
)
