package com.skintrack.app.platform

actual class ShareManager {
    actual suspend fun shareImage(imageBytes: ByteArray, text: String) {
        // Mock: log output for now
        println("ShareManager: Mock share image (${imageBytes.size} bytes), text=$text")
    }
}
