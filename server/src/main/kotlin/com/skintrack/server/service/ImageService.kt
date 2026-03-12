package com.skintrack.server.service

import com.skintrack.server.config.UploadsConfig
import java.io.File

class ImageService(private val config: UploadsConfig) {

    init {
        File(config.dir).mkdirs()
    }

    fun save(userId: String, fileName: String, bytes: ByteArray): String {
        val userDir = File(config.dir, userId).apply { mkdirs() }
        val file = File(userDir, fileName)
        file.writeBytes(bytes)
        return "${config.baseUrl}/uploads/$userId/$fileName"
    }
}
