package com.skintrack.app.platform

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

actual class ImageCompressor {
    /**
     * Compresses and resizes an image, stripping all EXIF metadata in the process.
     *
     * Security: The decode→Bitmap→compress pipeline inherently discards EXIF data
     * (GPS location, device info, timestamps, etc.) because [BitmapFactory.decodeByteArray]
     * only produces raw pixel data and [Bitmap.compress] writes a clean JPEG without
     * copying original file metadata. No additional EXIF stripping is needed.
     */
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
        // Re-encode through Bitmap.compress — this produces a clean JPEG
        // with no EXIF metadata from the original image.
        val output = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, quality, output)
        if (scaled !== original) scaled.recycle()
        original.recycle()
        return output.toByteArray()
    }
}
