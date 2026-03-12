package com.skintrack.app.platform

actual class NotificationManager {
    actual fun scheduleReminder(hour: Int, minute: Int) {
        println("NotificationManager: Mock schedule reminder at $hour:$minute")
    }

    actual fun cancelReminder() {
        println("NotificationManager: Mock cancel reminder")
    }
}
