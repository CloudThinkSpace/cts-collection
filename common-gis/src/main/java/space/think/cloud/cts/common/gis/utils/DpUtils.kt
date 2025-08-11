package space.think.cloud.cts.common.gis.utils

import android.content.Context

object DpUtils {

    fun dpToPx(context: Context, dp: Float): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }
}