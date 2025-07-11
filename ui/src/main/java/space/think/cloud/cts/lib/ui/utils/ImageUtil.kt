package space.think.cloud.cts.lib.ui.utils

import android.content.Context
import android.net.Uri
import space.think.cloud.cts.lib.watermark.Margin
import space.think.cloud.cts.lib.watermark.MimeType
import space.think.cloud.cts.lib.watermark.Padding
import space.think.cloud.cts.lib.watermark.Watermark
import space.think.cloud.cts.lib.watermark.utils.BitmapCompressor
import space.think.cloud.cts.lib.watermark.utils.BitmapUriLoader
import space.think.cloud.cts.lib.watermark.utils.BitmapUriWriter
import space.think.cloud.cts.lib.watermark.utils.UriFileDeleter

object ImageUtil {

    suspend fun printWatermark(
        context: Context,
        tableData: List<List<String>>,
        title: String = "实施日志",
        uri: Uri,
        onResult: (Uri?) -> Unit
    ) {

        // 1. 读取图片
        val originBitmap = BitmapUriLoader.loadBitmap(
            context, uri
        )

        originBitmap?.let {

            // 2. 创建水印bitmap
            val bitmap = Watermark(
                originalBitmap = originBitmap,
                tableData = tableData,
                headerTitle = title,
                margin = Margin(150f),
                textVerticalCenter = true,
                cellPadding = Padding(30f, 10f)
            ).draw()

            // 3. 删除文件
            UriFileDeleter.deleteFile(context, uri = uri)

            // 压缩图片
            val compressBitmap = BitmapCompressor.compressQuality(bitmap)

            // 4. 保存图片
            BitmapUriWriter.writeImage(
                context = context,
                bitmap = compressBitmap,
                displayName = "photo_${System.currentTimeMillis()}",
                mimeType = MimeType.JPEG,
                folderName = "cts",
                onResult = onResult,
            )
        }

    }
}