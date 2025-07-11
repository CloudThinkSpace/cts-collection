package space.think.cloud.cts.lib.watermark.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

object BitmapCompressor {
    private const val TAG = "BitmapCompressor"
    private const val DEFAULT_QUALITY = 75
    private const val DEFAULT_MAX_SIZE_KB = 300 //  512kb

    /**
     * 质量压缩 (兼容所有Android版本)
     * @param bitmap 原始位图
     * @param format 压缩格式 (JPEG/PNG/WEBP)
     * @param quality 质量 (0-100)
     * @param maxSizeKB 目标最大大小(KB)
     * @return 压缩后的Bitmap
     */
    @JvmOverloads
    fun compressQuality(
        bitmap: Bitmap,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = DEFAULT_QUALITY,
        maxSizeKB: Int = DEFAULT_MAX_SIZE_KB
    ): Bitmap {
        val outputStream = ByteArrayOutputStream()
        var currentQuality = quality.coerceIn(0, 100)

        // 渐进式压缩直到满足大小要求或质量低于10
        do {
            outputStream.reset()
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && format == Bitmap.CompressFormat.WEBP) {
                    // Android 11+ 支持无损WEBP
                    bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, currentQuality, outputStream)
                } else {
                    bitmap.compress(format, currentQuality, outputStream)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Compress error: ${e.message}")
                break
            }
            currentQuality -= 5
        } while (outputStream.size() > maxSizeKB * 1024 && currentQuality > 10)

        val options = BitmapFactory.Options().apply {
            // 防止OOM
            inPreferredConfig = Bitmap.Config.RGB_565
            inSampleSize = 1
        }

        return BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size())
            ?: bitmap // 如果解码失败返回原始bitmap
    }

    /**
     * 尺寸压缩 (兼容所有版本)
     * @param bitmap 原始位图
     * @param maxWidth 最大宽度
     * @param maxHeight 最大高度
     * @return 压缩后的Bitmap
     */
    fun compressSize(
        bitmap: Bitmap,
        maxWidth: Int,
        maxHeight: Int
    ): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxWidth && height <= maxHeight) {
            return bitmap
        }

        val scale = calculateScaleRatio(width, height, maxWidth, maxHeight)

        val matrix = Matrix()
        matrix.postScale(scale, scale)

        return try {
            Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        } catch (e: OutOfMemoryError) {
            Log.e(TAG, "OOM during size compression: ${e.message}")
            // 尝试更激进的压缩
            Bitmap.createScaledBitmap(bitmap, maxWidth, maxHeight, true)
        }
    }

    /**
     * 从URI加载并自动压缩 (兼容所有版本)
     */
    @JvmOverloads
    fun loadAndCompress(
        context: Context,
        uri: Uri,
        reqWidth: Int,
        reqHeight: Int,
        quality: Int = DEFAULT_QUALITY
    ): Bitmap? {
        var inputStream: InputStream? = null
        return try {
            // 第一次解码只获取尺寸
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
                // 使用更节省内存的配置
                inPreferredConfig = Bitmap.Config.RGB_565
            }

            inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            // 计算采样率
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)
            options.inJustDecodeBounds = false

            // 加载调整尺寸后的Bitmap
            inputStream = context.contentResolver.openInputStream(uri)
            var bitmap = BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            // 处理旋转（如果是照片）
            bitmap = bitmap?.let { correctRotation(context, it, uri) }

            // 质量压缩
            bitmap?.let { compressQuality(it, quality = quality) }
        } catch (e: Exception) {
            Log.e(TAG, "loadAndCompress error: ${e.message}")
            null
        } finally {
            try {
                inputStream?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Failed to close stream: ${e.message}")
            }
        }
    }

    /**
     * 保存压缩后的图片到文件 (兼容所有版本)
     */
    @JvmOverloads
    fun saveCompressedBitmap(
        bitmap: Bitmap,
        destination: File,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = DEFAULT_QUALITY
    ): Boolean {
        var outputStream: FileOutputStream? = null
        return try {
            outputStream = FileOutputStream(destination)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && format == Bitmap.CompressFormat.WEBP) {
                bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, quality, outputStream)
            } else {
                bitmap.compress(format, quality, outputStream)
            }
            true
        } catch (e: Exception) {
            Log.e(TAG, "saveCompressedBitmap error: ${e.message}")
            false
        } finally {
            try {
                outputStream?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Failed to close stream: ${e.message}")
            }
        }
    }

    // ============= 私有方法 =============

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height, width) = options.outHeight to options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // 计算最大的inSampleSize值，保持宽高大于请求尺寸
            while (halfHeight / inSampleSize >= reqHeight &&
                halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }

            // 对于老设备更保守的采样
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun calculateScaleRatio(
        originalWidth: Int,
        originalHeight: Int,
        maxWidth: Int,
        maxHeight: Int
    ): Float {
        val widthRatio = maxWidth.toFloat() / originalWidth
        val heightRatio = maxHeight.toFloat() / originalHeight
        return widthRatio.coerceAtMost(heightRatio).coerceAtLeast(0.1f) // 最小缩放0.1
    }

    private fun correctRotation(context: Context, bitmap: Bitmap, uri: Uri): Bitmap {
        return try {
            val input = context.contentResolver.openInputStream(uri) ?: return bitmap
            val exif = when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.N -> ExifInterface(input)
                else -> ExifInterface(uri.path ?: return bitmap)
            }

            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                else -> return bitmap
            }

            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: Exception) {
            Log.e(TAG, "correctRotation error: ${e.message}")
            bitmap
        }
    }
}