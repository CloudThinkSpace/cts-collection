package space.think.cloud.cts.collection

import com.google.gson.JsonObject
import org.maplibre.geojson.Feature
import org.maplibre.geojson.Point
import space.think.cloud.cts.lib.ui.task.TaskItem

fun taskItemToFeature(taskItem: TaskItem): Feature {
    return Feature.fromGeometry(
        Point.fromLngLat(taskItem.lon.toDouble(), taskItem.lat.toDouble()),
        JsonObject().apply {
            addProperty("code", taskItem.code)
            addProperty("taskId", taskItem.id)
            addProperty("lon", taskItem.lon)
            addProperty("lat", taskItem.lat)
            addProperty("name", taskItem.name)
            addProperty("status", taskItem.status)
        }
    )
}