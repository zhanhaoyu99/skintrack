package com.skintrack.app.platform

expect class NotificationManager() {
    fun scheduleReminder(hour: Int, minute: Int)
    fun cancelReminder()
}
