package space.think.cloud.cts.lib.form.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

// 文件上传工具类 (兼容所有Android版本)
object FileUploadUtils {

    private const val BUFFER_SIZE = 8192 // 8KB 缓冲区

    /**
     * 获取文件名和MIME类型
     */
    @SuppressLint("Range")
    fun getFileInfo(contentResolver: ContentResolver, uri: Uri): Pair<String?, String?> {
        var fileName: String? = null
        var mimeType: String? = contentResolver.getType(uri)

        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                // 尝试从不同列获取文件名
                fileName = when {
                    cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME) >= 0 ->
                        cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                    cursor.getColumnIndex("_display_name") >= 0 ->
                        cursor.getString(cursor.getColumnIndex("_display_name"))
                    cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME) >= 0 ->
                        cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                    else -> "image_${System.currentTimeMillis()}.jpg"
                }

                // 如果MIME类型为空，尝试从cursor获取
                if (mimeType == null) {
                    if (cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE) >= 0) {
                        mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE))
                    }
                }
            }
        }
        return fileName to mimeType
    }

    /**
     * 将URI转换为可上传的MultipartBody.Part
     */
    fun createImagePart(
        context: Context,
        uri: Uri
    ): MultipartBody.Part? {
        val contentResolver = context.contentResolver
        val (fileName, mimeType) = getFileInfo(contentResolver, uri)
        val safeFileName = fileName ?: "image_${System.currentTimeMillis()}.jpg"
        val safeMimeType = mimeType ?: "image/jpeg"

        return try {
            contentResolver.openInputStream(uri)?.use { inputStream ->
                // 创建临时文件（兼容所有Android版本）
                val tempFile = createTempFile(context, inputStream, safeFileName)

                // 创建请求体
                val requestBody = tempFile.asRequestBody(safeMimeType.toMediaTypeOrNull())

                // 创建Multipart部分
                MultipartBody.Part.createFormData("file", safeFileName, requestBody)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 创建临时文件（兼容所有Android版本）
     */
    private fun createTempFile(
        context: Context,
        inputStream: InputStream,
        fileName: String
    ): File {
        val outputDir = context.cacheDir
        // 清理文件名中的特殊字符
        val cleanFileName = fileName.replace(Regex("[^a-zA-Z0-9_.-]"), "_")
        val outputFile = File(outputDir, cleanFileName)

        FileOutputStream(outputFile).use { output ->
            val buffer = ByteArray(BUFFER_SIZE)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
            }
        }

        // 设置文件在应用退出时删除
        outputFile.deleteOnExit()
        return outputFile
    }
}