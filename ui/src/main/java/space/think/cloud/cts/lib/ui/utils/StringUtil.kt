package space.think.cloud.cts.lib.ui.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object StringUtil {

    fun <K, V> jsonToMap(json: String): Map<K, V> {
        if (json.isEmpty()) {
            return mapOf()
        }
        val gson = Gson()
        val type = object : TypeToken<Map<K, V>>() {}.type
        val dataMap: Map<K, V> = gson.fromJson(json, type)
        return dataMap
    }
}