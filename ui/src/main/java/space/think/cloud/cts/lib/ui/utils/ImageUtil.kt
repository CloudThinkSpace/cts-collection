package space.think.cloud.cts.lib.ui.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import space.think.cloud.cts.lib.watermark.Padding
import space.think.cloud.cts.lib.watermark.WatermarkFactory
import space.think.cloud.cts.lib.watermark.impl.TableWatermark
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

            val watermark = TableWatermark(
                tableData = tableData,
                headerTitle = title,
                textVerticalCenter = true,
                cellPadding = Padding(30f, 10f)
            )
            val bitmap = WatermarkFactory.create(watermark, it)

            // 3. 删除文件
            UriFileDeleter.deleteFile(context, uri = uri)

            // 压缩图片
            val pairBitmap = BitmapCompressor.compressQuality(bitmap)

            // 4. 保存图片
            val uri =  BitmapUriWriter.writeImage(
                context = context,
                bitmap = pairBitmap.first,
                displayName = "photo_${System.currentTimeMillis()}",
                format = Bitmap.CompressFormat.JPEG,
                folderName = "cts",
            )

            onResult(uri)
        }

    }
}