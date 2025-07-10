package space.think.cloud.cts.lib.watermark.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.IOException

object BitmapUriLoader {

    /**
     * 从URI加载Bitmap（自动处理不同类型URI）
     * @param context 上下文
     * @param uri 文件URI
     * @param reqWidth 目标宽度（可选，用于缩放）
     * @param reqHeight 目标高度（可选，用于缩放）
     */
    fun loadBitmap(
        context: Context,
        uri: Uri,
        reqWidth: Int = -1,
        reqHeight: Int = -1
    ): Bitmap? {
        return try {
            when {
                // 如果需要缩放
                reqWidth > 0 && reqHeight > 0 -> {
                    loadScaledBitmap(context, uri, reqWidth, reqHeight)
                }
                // Android 10+ 推荐使用FileDescriptor
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ->  {
                    loadBitmapFromFileProvider(context, uri)
                }
                // 默认处理方式
                else -> {
                    loadBitmapWithFallback(context, uri)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun loadScaledBitmap(
        context: Context,
        uri: Uri,
        reqWidth: Int,
        reqHeight: Int
    ): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
            decodeUri(context, uri, this)
            inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)
            inJustDecodeBounds = false
        }

        return decodeUri(context, uri, options)
    }

    private fun loadBitmapFromFileProvider(
        context: Context,
        uri: Uri
    ): Bitmap? {
        return context.contentResolver.openFileDescriptor(uri, "r")?.use {
            BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
        }
    }

    private fun loadBitmapWithFallback(
        context: Context,
        uri: Uri
    ): Bitmap? {
        return try {
            // 先尝试直接解码
            decodeUri(context, uri, null) ?: run {
                // 如果失败，尝试其他方法
                when {
                    uri.scheme == "file" -> {
                        BitmapFactory.decodeFile(uri.path)
                    }
                    uri.toString().contains(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString()) -> {
                        loadFromMediaStore(context, uri)
                    }
                    else -> null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun loadFromMediaStore(
        context: Context,
        uri: Uri
    ): Bitmap? {
        return try {
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        } catch (e: IOException) {
            null
        }
    }

    private fun decodeUri(
        context: Context,
        uri: Uri,
        options: BitmapFactory.Options?
    ): Bitmap? {
        return context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height, width) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}