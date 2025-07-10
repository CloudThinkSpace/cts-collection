package space.think.cloud.cts.lib.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import java.io.File

object FileUtil {

    @SuppressLint("ObsoleteSdkInt")
    fun getUriFromPath(context: Context, filePath: String): Uri? {
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
            e.printStackTrace()
            null
        }
    }
}