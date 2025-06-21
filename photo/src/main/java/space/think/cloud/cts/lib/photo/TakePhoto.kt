package space.think.cloud.cts.lib.photo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.FileProvider
import java.io.File
import java.util.UUID

class TakePhoto(private val mediaStoreType: String) :
    ActivityResultContract<Unit?, PictureResult>() {

    private var outUri: Uri? = null
    private var imageName: String? = null

    override fun createIntent(context: Context, input: Unit?): Intent =
        Intent(mediaStoreType).also { intent ->
            getFileDirectory(context)?.let {
                outUri = it
                intent.putExtra(MediaStore.EXTRA_OUTPUT, it).apply {
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                }
            }

        }

    override fun parseResult(resultCode: Int, intent: Intent?): PictureResult {
        return PictureResult(outUri, resultCode == Activity.RESULT_OK)
    }

    private fun getFileDirectory(context: Context): Uri? {//获取app内部文件夹
        imageName = "img_${UUID.randomUUID().toString().substring(0, 7)}"
        val fileFolder = File(context.cacheDir, "cts_imgs")
        if (!fileFolder.exists()) {
            fileFolder.mkdirs()
        }
        val ext = if (mediaStoreType == MediaStore.ACTION_IMAGE_CAPTURE) "jpeg" else "mp4"
        val file = File(fileFolder, "${imageName}.${ext}")
        if (!file.exists()) {
            file.createNewFile()
        }
        return FileProvider.getUriForFile(context, "${context.packageName}.image.provider", file)
    }
}