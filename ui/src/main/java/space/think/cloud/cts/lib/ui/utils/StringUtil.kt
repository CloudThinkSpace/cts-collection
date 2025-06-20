package space.think.cloud.cts.lib.ui.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import space.think.cloud.cts.lib.ui.form.ImageItem

object StringUtil {

    fun jsonToMap(json: String): Map<Int, ImageItem> {
        if (json.isEmpty()) {
            return mapOf()
        }
        val gson = Gson()
        val type = object : TypeToken<Map<Int, ImageItem>>() {}.type
        val dataMap: Map<Int, ImageItem> = gson.fromJson(json, type)
        return dataMap
    }

    fun <K, V> mapToString(map: Map<K, V>): String {
        val gson = Gson()
        return gson.toJson(map)
    }
}