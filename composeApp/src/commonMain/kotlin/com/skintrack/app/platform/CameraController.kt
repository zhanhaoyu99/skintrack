package com.skintrack.app.platform

expect class CameraController {
    suspend fun takePhoto(): ByteArray?
    fun isAvailable(): Boolean
}
