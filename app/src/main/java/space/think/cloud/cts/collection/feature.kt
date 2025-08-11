package space.think.cloud.cts.collection

import com.google.gson.JsonObject
import org.maplibre.geojson.Feature
import org.maplibre.geojson.Point
import space.think.cloud.cts.common.gis.Marker
import space.think.cloud.cts.lib.ui.task.TaskItem

fun taskItemToFeature(taskItem: TaskItem): Feature {
    return Feature.fromGeometry(
        Point.fromLngLat(taskItem.lon.toDouble(), taskItem.lat.toDouble()),
        JsonObject().apply {
            addProperty(Marker.CODE, taskItem.code)
            addProperty(Marker.TASK_ID, taskItem.id)
            addProperty(Marker.LOT, taskItem.lon)
            addProperty(Marker.LAT, taskItem.lat)
            addProperty(Marker.NAME, taskItem.name)
            addProperty(Marker.STATUS, taskItem.status)
        }
    )
}