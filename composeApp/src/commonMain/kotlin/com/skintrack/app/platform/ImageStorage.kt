package com.skintrack.app.platform

expect class ImageStorage() {
    suspend fun saveImage(imageBytes: ByteArray, fileName: String): String
}

expect fun pathToImageModel(localImagePath: String): Any
