package com.skintrack.app.platform

expect class ImageStorage() {
    suspend fun saveImage(imageBytes: ByteArray, fileName: String): String
    suspend fun loadImage(localPath: String): ByteArray?
}

expect fun pathToImageModel(localImagePath: String): Any
