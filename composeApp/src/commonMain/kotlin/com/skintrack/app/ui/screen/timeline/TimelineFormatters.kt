package com.skintrack.app.ui.screen.timeline

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun formatRecordDate(instant: Instant): String {
    val dt = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    val minute = dt.minute.toString().padStart(2, '0')
    return "${dt.year}年${dt.monthNumber}月${dt.dayOfMonth}日 ${dt.hour}:$minute"
}
