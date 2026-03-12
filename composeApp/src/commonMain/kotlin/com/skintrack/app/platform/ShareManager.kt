package com.skintrack.app.platform

expect class ShareManager() {
    suspend fun shareImage(imageBytes: ByteArray, text: String)
}
