package com.skintrack.app.platform

import io.github.aakira.napier.Napier

actual class NotificationManager {
    actual fun scheduleReminder(hour: Int, minute: Int) {
        Napier.d("NotificationManager: Mock schedule reminder at $hour:$minute", tag = "Notification")
    }

    actual fun cancelReminder() {
        Napier.d("NotificationManager: Mock cancel reminder", tag = "Notification")
    }

    actual fun registerDeviceToken(onToken: (String) -> Unit) {
        // TODO: Integrate Firebase Cloud Messaging to get real FCM token
        // FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
        //     if (task.isSuccessful) {
        //         val token = task.result
        //         onToken(token)
        //     }
        // }
        val mockToken = "mock-fcm-token-android-${System.currentTimeMillis()}"
        Napier.d("NotificationManager: Mock FCM token = $mockToken", tag = "Notification")
        onToken(mockToken)
    }
}
