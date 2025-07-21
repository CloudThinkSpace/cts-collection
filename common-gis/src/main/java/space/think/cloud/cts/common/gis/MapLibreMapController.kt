package space.think.cloud.cts.common.gis

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import org.maplibre.android.annotations.IconFactory
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapLibreMap.CancelableCallback
import org.maplibre.android.style.layers.RasterLayer
import space.think.cloud.cts.common.gis.layer.setupTiandituStyle
import space.think.cloud.cts.common.gis.source.cts.RasterSourceBuilder

class MapLibreMapController(
    val context: Context,
    val mapLibreMap: MapLibreMap
) : MapController {


    init {
        // 初始化天地图
        setupTiandituStyle(mapLibreMap, "e6ed3fdaf6ca24a041d8dcb69ab279f2")
    }

    override fun <T> addMarker(marker: CtsMarker): T {
        val bitmap = drawableToBitmap(context, marker.icon)
        val markerIcon = IconFactory.getInstance(context).fromBitmap(bitmap)
        val markerOptions = MarkerOptions().apply {
            position = LatLng(marker.lat, marker.lon)
            title = marker.title
            snippet = marker.description
            icon = markerIcon
        }
        return mapLibreMap.addMarker(markerOptions) as T
    }

    override fun getExtent() {
        TODO("Not yet implemented")
    }

    override fun addLayer(name: String, uri: String) {

        val rasterSource = RasterSourceBuilder()
            .withId("$name-source")
            .withUri(Uri.decode(uri))
            .build()

        mapLibreMap.style?.addSource(rasterSource)
        mapLibreMap.style?.addLayer(RasterLayer("$name-layer", "$name-source"))
    }

    fun onInfoWindowClickListener(onClick: (Marker) -> Unit) {
        mapLibreMap.onInfoWindowClickListener = object : MapLibreMap.OnInfoWindowClickListener {
            override fun onInfoWindowClick(marker: Marker): Boolean {
                onClick(marker)
                return true
            }
        }
    }

    fun drawableToBitmap(context: Context, drawableResId: Int): Bitmap {
        return BitmapFactory.decodeResource(context.resources, drawableResId)
    }

    fun setCameraPosition(cameraPosition: CameraPosition) {
        mapLibreMap.cameraPosition = cameraPosition
    }

    fun animateCamera(cameraPosition: CameraPosition, delay: Int = 2000) {
        mapLibreMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), delay)
    }

    fun animateToLatLng(
        latLng: LatLng,
        zoom: Double = 10.0,
        delay: Int = 2000,
        onFinish: (() -> Unit)? = null
    ) {
        // 移动到点位
        val cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(zoom)
            .build()

        mapLibreMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(cameraPosition), delay,
            object : CancelableCallback {
                override fun onCancel() {
                }

                override fun onFinish() {
                    onFinish?.invoke()
                }
            }
        )
    }

    fun animateToBounds(
        bounds: List<LatLng>,
        delay: Int = 2000,
        onFinish: (() -> Unit)? = null
    ) {
        mapLibreMap.getCameraForLatLngBounds(LatLngBounds.fromLatLngs(bounds))?.let {
            val newCameraPosition = CameraPosition.Builder()
                .target(it.target)
                .zoom(it.zoom - 0.5)
                .build()
            mapLibreMap.animateCamera(
                CameraUpdateFactory.newCameraPosition(newCameraPosition), delay,
                object : CancelableCallback {
                    override fun onCancel() {
                    }

                    override fun onFinish() {
                        onFinish?.invoke()
                    }
                }
            )
        }
    }

    // 显示InfoWindow
    fun showInfoWindow(marker: CtsMarker) {
        val bitmap = drawableToBitmap(context, marker.icon)
        val markerIcon = IconFactory.getInstance(context).fromBitmap(bitmap)
        val markerOptions = MarkerOptions().apply {
            position = LatLng(marker.lat, marker.lon)
            title = marker.title
            snippet = marker.description
            icon = markerIcon
        }
        mapLibreMap.selectMarker(Marker(markerOptions))
    }

    // 显示InfoWindow
    fun showInfoWindow(marker: Marker) {
        mapLibreMap.selectMarker(marker)
    }

    // 隐藏InfoWindow
    fun hideInfoWindow() {
        mapLibreMap.deselectMarkers()
    }

    // 切换显示状态
    fun toggleInfoWindow(marker: Marker) {
        if (marker.isInfoWindowShown) {
            mapLibreMap.deselectMarker(marker)
        } else {
            mapLibreMap.selectMarker(marker)
        }
    }


}