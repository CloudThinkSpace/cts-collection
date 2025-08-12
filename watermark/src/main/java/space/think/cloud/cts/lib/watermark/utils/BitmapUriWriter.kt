package space.think.cloud.cts.lib.watermark.utils

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date
import java.util.Locale


object BitmapUriWriter {

    /**
     * 兼容所有Android版本的图片保存方法
     * @param context 上下文
     * @param bitmap 要保存的Bitmap
     * @param displayName 文件名 (不带扩展名)
     * @param format 图片类型 ("image/jpeg", "image/png")
     * @param folderName 子文件夹名称 (可选)
     */
    suspend fun writeImage(
        context: Context,
        bitmap: Bitmap,
        displayName: String,
        format: Bitmap.CompressFormat,
        folderName: String? = null,
    ): Uri? {
        val result = saveToGallery(
            context = context,
            bitmap = bitmap,
            displayName = displayName,
            folderName = folderName,
            format = format
        )

        return result
    }

    /**
     * Android 10+ 使用MediaStore保存
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    @Suppress("unused")
    private fun saveViaMediaStore(
        context: Context,
        bitmap: Bitmap,
        displayName: String,
        mimeType: String,
        folderName: String?
    ): Uri? {
        val resolver = context.contentResolver
        val extension = when (mimeType) {
            "image/jpeg" -> ".jpg"
            "image/png" -> ".png"
            else -> ".jpg"
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$displayName$extension")
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + (folderName?.let { "/$it" } ?: ""))
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }

        val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            ?: return null

        return try {
            resolver.openOutputStream(uri)?.use { outputStream ->
                if (!bitmap.compress(
                        getCompressFormat(mimeType),
                        getQuality(mimeType),
                        outputStream
                    )
                ) {
                    throw IOException("Failed to save bitmap")
                }
            }

            // 标记为不再pending
            contentValues.clear()
            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)

            uri
        } catch (_: Exception) {
            resolver.delete(uri, null, null)
            null
        }
    }

    /**
     * 将Bitmap保存到系统相册
     * @param context 上下文
     * @param bitmap 要保存的Bitmap
     * @param format 图片格式（JPEG/PNG）
     * @param quality 质量（0-100，仅JPEG有效）
     * @param displayName 显示名称（不含扩展名）
     * @return 保存的图片URI，失败返回null
     */
    fun saveToGallery(
        context: Context,
        bitmap: Bitmap,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.JPEG,
        quality: Int = 90,
        displayName: String? = null,
        folderName: String?
    ): Uri? {
        // 生成文件名
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = displayName ?: "IMG_$timeStamp"
        val extension = if (format == Bitmap.CompressFormat.JPEG) "jpg" else "png"
        val mimeType = if (format == Bitmap.CompressFormat.JPEG) "image/jpeg" else "image/png"

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ 使用MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.$extension")
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + (folderName?.let { "/$it" } ?: ""))
                    put(MediaStore.MediaColumns.IS_PENDING, 1) // 标记为待处理
                }

                val resolver: ContentResolver = context.contentResolver
                val collection =
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                var uri: Uri? = resolver.insert(collection, contentValues)

                uri?.let {
                    resolver.openOutputStream(it)?.use { outputStream ->
                        // 写入图片数据
                        if (bitmap.compress(format, quality, outputStream)) {
                            // 完成保存，更新状态
                            contentValues.clear()
                            contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                            resolver.update(it, contentValues, null, null)
                            return it
                        } else {
                            // 保存失败，删除空记录
                            resolver.delete(it, null, null)
                            uri = null
                        }
                    }
                }
                uri
            } else {
                // Android 9及以下，直接保存到公共目录
                val picturesDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val file = File(picturesDir, "$fileName.$extension")

                // 确保目录存在
                if (!picturesDir.exists()) {
                    picturesDir.mkdirs()
                }

                FileOutputStream(file).use { outputStream ->
                    if (bitmap.compress(format, quality, outputStream)) {
                        // 通知系统相册更新
                        MediaStore.Images.Media.insertImage(
                            context.contentResolver,
                            file.absolutePath,
                            file.name,
                            null
                        )
                        // 发送广播刷新媒体库
                        context.sendBroadcast(
                            android.content.Intent(
                                android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                Uri.fromFile(file)
                            )
                        )
                        return Uri.fromFile(file)
                    }
                }
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Android 9及以下使用传统文件API保存
     */
    @Suppress("unused")
    private fun saveViaFileApi(
        bitmap: Bitmap,
        displayName: String,
        mimeType: String,
        folderName: String?
    ): File? {
        // 检查存储是否可用
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return null
        }

        val extension = when (mimeType) {
            "image/jpeg" -> ".jpg"
            "image/png" -> ".png"
            else -> ".jpg"
        }

        // 创建目标目录
        val baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val saveDir = if (folderName != null) File(baseDir, folderName) else baseDir

        if (!saveDir.exists() && !saveDir.mkdirs()) {
            return null
        }

        // 创建目标文件
        val imageFile = File(saveDir, "$displayName$extension")

        return try {
            FileOutputStream(imageFile).use { outputStream ->
                if (!bitmap.compress(
                        getCompressFormat(mimeType),
                        getQuality(mimeType),
                        outputStream
                    )
                ) {
                    return null
                }
                outputStream.flush()
            }
            imageFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    private fun getCompressFormat(mimeType: String): Bitmap.CompressFormat {
        return when (mimeType) {
            "image/jpeg" -> Bitmap.CompressFormat.JPEG
            "image/png" -> Bitmap.CompressFormat.PNG
            else -> Bitmap.CompressFormat.JPEG
        }
    }

    private fun getQuality(mimeType: String): Int {
        return if (mimeType == "image/png") 100 else 90
    }

}