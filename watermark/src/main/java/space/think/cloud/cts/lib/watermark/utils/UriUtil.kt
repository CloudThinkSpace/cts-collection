package space.think.cloud.cts.lib.watermark.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File
import androidx.core.net.toUri

object UriUtil {
    /**
     * 安全地将字符串转换为Uri
     */
    fun safeParse(uriString: String?): Uri? {
        return try {
            uriString?.let { it.toUri() }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 从文件路径获取Uri（兼容所有Android版本）
     */
    fun fromFilePath(context: Context, filePath: String): Uri? {
        return try {
            val file = File(filePath)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            } else {
                Uri.fromFile(file)
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 检查Uri是否有效
     */
    fun isValid(uri: Uri?): Boolean {
        return uri != null && uri != Uri.EMPTY
    }
}