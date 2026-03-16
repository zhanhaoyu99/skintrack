package com.skintrack.server.service

import com.skintrack.server.config.UploadsConfig
import java.io.File
import java.util.UUID

class ImageService(private val config: UploadsConfig) {

    companion object {
        private val ALLOWED_EXTENSIONS = setOf("jpg", "jpeg", "png", "webp")
        private const val MAX_FILE_SIZE = 10 * 1024 * 1024 // 10MB
    }

    init {
        File(config.dir).mkdirs()
    }

    fun save(userId: String, originalFileName: String, bytes: ByteArray): String {
        // Validate file size
        if (bytes.size > MAX_FILE_SIZE) {
            throw IllegalArgumentException("文件大小不能超过 10MB")
        }

        // Validate file extension
        val extension = originalFileName.substringAfterLast('.', "").lowercase()
        if (extension !in ALLOWED_EXTENSIONS) {
            throw IllegalArgumentException("仅支持 jpg/jpeg/png/webp 格式的图片")
        }

        // Use UUID for filename to prevent path traversal
        val safeFileName = "${UUID.randomUUID()}.$extension"
        val userDir = File(config.dir, userId).apply { mkdirs() }
        val file = File(userDir, safeFileName)
        file.writeBytes(bytes)
        return "${config.baseUrl}/uploads/$userId/$safeFileName"
    }
}
