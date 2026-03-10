package com.skintrack.app.platform

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

actual class ImageCompressor {
    actual suspend fun compress(imageBytes: ByteArray, quality: Int, maxWidth: Int): ByteArray {
        val original = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        val ratio = maxWidth.toFloat() / original.width
        val scaled = if (ratio < 1f) {
            Bitmap.createScaledBitmap(
                original,
                maxWidth,
                (original.height * ratio).toInt(),
                true,
            )
        } else {
            original
        }
        val output = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, quality, output)
        return output.toByteArray()
    }
}
