package com.skintrack.app.platform

interface CameraController {
    suspend fun takePhoto(): ByteArray?
}
