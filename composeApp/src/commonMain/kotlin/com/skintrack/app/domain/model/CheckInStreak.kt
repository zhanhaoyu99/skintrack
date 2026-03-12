package com.skintrack.app.domain.model

import kotlinx.datetime.LocalDate

data class CheckInStreak(
    val userId: String,
    val currentStreak: Int,
    val longestStreak: Int,
    val lastCheckInDate: LocalDate?,
)
