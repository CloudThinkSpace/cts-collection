package space.think.cloud.cts.lib.watermark.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import androidx.core.graphics.createBitmap
import java.io.ByteArrayOutputStream
import kotlin.math.sqrt

object BitmapCompressor {

    private const val MAX_SIZE_KB = 150 // 目标最大大小：300KB
    private const val MAX_SIZE_BYTES = MAX_SIZE_KB * 1024 // 转换为字节
    private const val MIN_QUALITY = 30 // 最小质量值（避免过度压缩导致严重失真）

    /**
     * 质量压缩 (兼容所有Android版本)
     * @param source 原始位图
     * @param format 压缩格式 (JPEG/PNG/WEBP)
     * @param maxSizeKB 目标最大大小(KB)
     * @param minQuality 目标最小压缩质量，默认30
     * @return 压缩后的Bitmap
     */
    @JvmOverloads
    fun compressQuality(
        source: Bitmap,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        maxSizeKB: Int = MAX_SIZE_BYTES,
        minQuality: Int = MIN_QUALITY,
    ): Pair<Bitmap, Int> {
        // 先尝试质量压缩
        var quality = 100
        var compressedBytes = bitmapToByteArray(source, format, quality)

        // 质量压缩循环（仅对JPEG有效，PNG会直接跳过）
        if (format == Bitmap.CompressFormat.JPEG) {
            while (compressedBytes.size > maxSizeKB && quality > minQuality) {
                quality -= 5 // 每次降低5%质量
                compressedBytes = bitmapToByteArray(source, format, quality)
            }
        }
        // 如果质量压缩后仍超标，则进行尺寸压缩
        return if (compressedBytes.size <= maxSizeKB) {
            // 质量压缩已满足要求
            val resultBitmap =
                BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)
            Pair(resultBitmap, quality)
        } else {
            // 需要结合尺寸压缩
            val scaleRatio = sqrt(maxSizeKB.toDouble() / compressedBytes.size).toFloat()
            var currentScale = scaleRatio.coerceAtMost(0.9f)

            var scaledBitmap = source
            var attempts = 0

            while (true) {
                // 回收上一次的缩放Bitmap
                if (attempts > 0 && scaledBitmap != source) {
                    scaledBitmap.recycle()
                }

                // 计算新尺寸
                val newWidth = (source.width * currentScale).toInt()
                val newHeight = (source.height * currentScale).toInt()

                // 执行缩放
                scaledBitmap = scaleBitmap(source, newWidth, newHeight)

                // 再次应用质量压缩
                compressedBytes = bitmapToByteArray(scaledBitmap, format, quality)

                // 检查是否满足条件或达到最大尝试次数
                if (compressedBytes.size <= maxSizeKB || attempts >= 8) {
                    break
                }

                currentScale *= 0.9f
                attempts++
            }

            val resultBitmap =
                BitmapFactory.decodeByteArray(compressedBytes, 0, compressedBytes.size)
            Pair(resultBitmap, quality)
        }

    }

    /**
     * 将Bitmap转换为字节数组（带质量参数）
     */
    private fun bitmapToByteArray(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat,
        quality: Int
    ): ByteArray {
        return ByteArrayOutputStream().use { outputStream ->
            bitmap.compress(format, quality, outputStream)
            outputStream.toByteArray()
        }
    }

    /**
     * 高质量缩放Bitmap到指定尺寸
     */
    private fun scaleBitmap(source: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        return if (source.config == null) source else {
            createBitmap(newWidth, newHeight, source.config!!).apply {
                val canvas = Canvas(this)
                val paint = Paint().apply {
                    isAntiAlias = true
                    isFilterBitmap = true
                    isDither = true
                }
                canvas.drawBitmap(
                    source,
                    Matrix().apply {
                        postScale(
                            newWidth.toFloat() / source.width,
                            newHeight.toFloat() / source.height
                        )
                    },
                    paint
                )
            }
        }
    }
}