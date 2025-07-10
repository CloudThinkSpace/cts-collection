package space.think.cloud.cts.lib.watermark.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import space.think.cloud.cts.lib.watermark.MimeType
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


object BitmapUriWriter {

    /**
     * 兼容所有Android版本的图片保存方法
     * @param context 上下文
     * @param bitmap 要保存的Bitmap
     * @param displayName 文件名 (不带扩展名)
     * @param mimeType 图片类型 ("image/jpeg", "image/png")
     * @param folderName 子文件夹名称 (可选)
     * @param onResult 保存结果回调 (成功返回Uri,失败返回null)
     */
    fun writeImage(
        context: Context,
        bitmap: Bitmap,
        displayName: String,
        mimeType: MimeType,
        folderName: String? = null,
        onResult: (Uri?) -> Unit
    ) {

        val mimeType = when (mimeType) {
            MimeType.JPEG -> MimeType.JPEG.type
            MimeType.PNG -> MimeType.PNG.type
        }
        // 在IO线程执行保存操作
        CoroutineScope(Dispatchers.IO).launch {
            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ 使用MediaStore API
                saveViaMediaStore(context, bitmap, displayName, mimeType, folderName)
            } else {
                // Android 9及以下使用传统文件API
                saveViaFileApi(bitmap, displayName, mimeType, folderName)?.let {
                    Uri.fromFile(it)
                }
            }

            // 返回主线程回调结果
            withContext(Dispatchers.Main) {
                onResult(result)
            }
        }
    }

    /**
     * Android 10+ 使用MediaStore保存
     */
    @RequiresApi(Build.VERSION_CODES.Q)
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
     * Android 9及以下使用传统文件API保存
     */
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