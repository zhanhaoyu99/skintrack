package com.skintrack.app.platform

expect class ImageCompressor() {
    suspend fun compress(imageBytes: ByteArray, quality: Int = 80, maxWidth: Int = 1080): ByteArray
}
