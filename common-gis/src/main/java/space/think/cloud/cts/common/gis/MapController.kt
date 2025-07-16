package space.think.cloud.cts.common.gis

interface MapController {

    fun addMarker(marker: CtsMarker)
    fun getExtent()

}