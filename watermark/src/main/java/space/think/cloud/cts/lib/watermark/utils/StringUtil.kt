package space.think.cloud.cts.lib.watermark.utils

import kotlin.math.ceil

object StringUtil {

    // 计算字符串被分割多少行
    fun lines(content:String, maxLength:Int):Int {
        return ceil(content.length / maxLength.toFloat()).toInt()
    }
}