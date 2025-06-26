package space.think.cloud.cts.lib.watermark

import android.graphics.Bitmap

// 水印接口
interface IWatermark {
    fun draw(originalBitmap: Bitmap): Bitmap
}