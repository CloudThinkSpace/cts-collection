package space.think.cloud.cts.lib.watermark.utils

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object UriFileDeleter {

    /**
     * 安全删除URI对应的文件
     * @param context 上下文
     * @param uri 文件URI
     * @return 是否删除成功
     */
    fun deleteFile(context: Context, uri: Uri): Boolean {
        return when {
            isContentUri(uri) -> deleteContentUriFile(context, uri)
            isFileUri(uri) -> deleteFileUri(uri)
            else -> false
        }
    }

    private fun isContentUri(uri: Uri): Boolean {
        return uri.scheme.equals("content", ignoreCase = true)
    }

    private fun isFileUri(uri: Uri): Boolean {
        return uri.scheme.equals("file", ignoreCase = true)
    }

    private fun deleteContentUriFile(context: Context, uri: Uri): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deleteContentUriFileQ(context, uri)
        } else {
            try {
                // 先尝试直接删除
                val rowsDeleted = context.contentResolver.delete(uri, null, null)
                if (rowsDeleted > 0) return true

                // 如果直接删除失败，尝试通过文件路径删除
                deleteViaFilePath(context, uri)
            } catch (_: Exception) {
                false
            }
        }
    }

    @androidx.annotation.RequiresApi(Build.VERSION_CODES.Q)
    private fun deleteContentUriFileQ(context: Context, uri: Uri): Boolean {
        return try {
            val collection = when {
                uri.toString().contains(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString()) -> {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                }
                uri.toString().contains(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString()) -> {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                }
                uri.toString().contains(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()) -> {
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                uri.toString().contains(MediaStore.Downloads.EXTERNAL_CONTENT_URI.toString()) -> {
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI
                }
                else -> null
            }

            if (collection != null) {
                val selection = "${MediaStore.MediaColumns._ID} = ?"
                val selectionArgs = arrayOf(ContentUris.parseId(uri).toString())
                context.contentResolver.delete(collection, selection, selectionArgs) > 0
            } else {
                context.contentResolver.delete(uri, null, null) > 0
            }
        } catch (_: Exception) {
            false
        }
    }

    private fun deleteFileUri(uri: Uri): Boolean {
        return try {
            val file = File(uri.path ?: return false)
            file.exists() && file.delete()
        } catch (_: Exception) {
            false
        }
    }

    private fun deleteViaFilePath(context: Context, uri: Uri): Boolean {
        return try {
            val file = uriToFile(context, uri)
            file.delete()
        } catch (_: Exception) {
            false
        }
    }

    @Throws(IOException::class)
    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri) ?: throw IOException("无法打开输入流")
        val file = createTempFile(context)
        FileOutputStream(file).use { output ->
            inputStream.copyTo(output)
        }
        return file
    }

    private fun createTempFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = context.externalCacheDir ?: context.cacheDir
        return File.createTempFile("temp_$timeStamp", null, storageDir)
    }
}