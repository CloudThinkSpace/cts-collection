package space.think.cloud.cts.common.gis

interface MapController {

    fun <T> addMarker(marker: CtsMarker): T
    fun addLayer(name: String, uri: String)
    fun getExtent()

}