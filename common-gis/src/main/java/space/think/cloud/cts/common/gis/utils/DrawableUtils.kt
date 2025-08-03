package space.think.cloud.cts.common.gis.utils

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import org.maplibre.android.annotations.Icon

object DrawableUtils {

    @JvmStatic
    fun drawableToIcon(
        context: Context,
        @DrawableRes id: Int,
        @ColorInt colorRes: Int? = null
    ): Icon {
        return IconUtils.drawableToIcon(context, id, colorRes)
    }

    @JvmStatic
    fun drawableToBitmap(
        context: Context,
        @DrawableRes id: Int,
        @ColorInt colorRes: Int? = null
    ): Bitmap {

        return BitmapUtils.drawableToBitmap(context, id, colorRes)
    }

}