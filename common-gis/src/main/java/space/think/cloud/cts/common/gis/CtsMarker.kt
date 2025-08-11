package space.think.cloud.cts.common.gis

import com.google.gson.JsonObject

data class CtsMarker(
    val taskId: String,
    val lon: Double,
    val lat: Double,
    val code: String,
    val title: String,
    val description: String,
) {

    companion object{
        fun fromJson(properties: JsonObject): CtsMarker {
            val code = properties.get(Marker.CODE)?.asString ?: "无标题"
            val taskId = properties.get(Marker.TASK_ID)?.asString ?: ""
            val lon = properties.get(Marker.LOT)?.asString ?: "0.0"
            val lat = properties.get(Marker.LAT)?.asString ?: "0.0"
            val description = properties.get(Marker.NAME)?.asString ?: ""
            return CtsMarker(
                taskId = taskId,
                code = code,
                title = code,
                lat = lat.toDouble(),
                lon = lon.toDouble(),
                description = description
            )
        }
    }

}

object Marker {
    const val LOT = "lon"
    const val LAT = "lat"
    const val TASK_ID = "taskId"
    const val NAME = "name"
    const val STATUS = "status"
    const val CODE = "code"
}