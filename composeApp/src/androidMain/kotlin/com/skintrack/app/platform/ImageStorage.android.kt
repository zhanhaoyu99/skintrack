package com.skintrack.app.platform

import com.skintrack.app.data.local.applicationContext
import java.io.File

actual class ImageStorage {
    actual suspend fun saveImage(imageBytes: ByteArray, fileName: String): String {
        val dir = File(applicationContext.filesDir, "skin_photos")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, fileName)
        file.writeBytes(imageBytes)
        return file.absolutePath
    }
}

actual fun pathToImageModel(localImagePath: String): Any = File(localImagePath)
