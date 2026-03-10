package com.skintrack.app.platform

actual class CameraController {
    actual suspend fun takePhoto(): ByteArray? {
        // TODO: Implement with CameraX
        return null
    }

    actual fun isAvailable(): Boolean {
        // TODO: Check camera hardware availability
        return true
    }
}
