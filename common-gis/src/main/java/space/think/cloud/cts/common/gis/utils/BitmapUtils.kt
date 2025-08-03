package space.think.cloud.cts.common.gis.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.DrawableCompat

object BitmapUtils {

    @JvmStatic
    fun drawableToBitmap(context: Context, @DrawableRes id: Int, @ColorInt colorRes: Int? = null): Bitmap {
        val vectorDrawable = ResourcesCompat.getDrawable(context.resources, id, context.theme)
        val bitmap = createBitmap(vectorDrawable!!.intrinsicWidth, vectorDrawable.intrinsicHeight)
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        colorRes?.let {
            DrawableCompat.setTint(vectorDrawable, colorRes)
        }
        vectorDrawable.draw(canvas)
        return bitmap
    }
}