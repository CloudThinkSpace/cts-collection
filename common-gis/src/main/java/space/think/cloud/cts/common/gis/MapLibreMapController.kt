package space.think.cloud.cts.common.gis

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
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
import org.maplibre.android.style.expressions.Expression
import org.maplibre.android.style.layers.Layer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.layers.RasterLayer
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.style.sources.Source
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
import space.think.cloud.cts.common.gis.layer.setupTiandituStyle
import space.think.cloud.cts.common.gis.source.cts.RasterSourceBuilder

class MapLibreMapController(
    val context: Context,
    val mapLibreMap: MapLibreMap
) : MapController {

    private val defaultMarkerSymbolName = "cts-marker"

    init {
        // 初始化天地图
        setupTiandituStyle(mapLibreMap, "e6ed3fdaf6ca24a041d8dcb69ab279f2")
        // 添加地图点击事件
        mapLibreMap.addOnMapClickListener {

            handleMapClick(mapLibreMap.projection.toScreenLocation(it))
            true
        }
    }

    private fun handleMapClick(clickPoint: PointF) {
        // 1. 获取点击位置的要素
        val features =
            mapLibreMap.queryRenderedFeatures(clickPoint, "$defaultMarkerSymbolName-symbol-layer")

        if (features.isNotEmpty()) {
            // 2. 获取第一个要素的属性
            val feature = features[0]
            val properties = feature.properties()
            val title = properties?.get("code")?.asString ?: "无标题"

            // 3. 获取要素的几何位置
            val point = feature.geometry() as Point
            val latLng = LatLng(point.latitude(), point.longitude())

            // 4. 显示自定义InfoWindow
            val ctsMarker = CtsMarker(
                lon = latLng.longitude,
                lat = latLng.latitude,
                title = title,
                description = "",
                icon = R.drawable.location_blue
            )
            showInfoWindow(ctsMarker)
        } else {
            // 点击了非标记区域，隐藏InfoWindow
            hideInfoWindow()
        }
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

    fun addSymbolLayer(
        name: String = defaultMarkerSymbolName,
        expression: Expression,
        features: List<Feature>
    ) {
        // 构造geoJson数据源
        val source = GeoJsonSource("$name-source", FeatureCollection.fromFeatures(features))
        // 添加数据源
        addSource(source)
        // 创建样式图层
        val layer = SymbolLayer("$name-symbol-layer", "$name-source")
            .withProperties(
                PropertyFactory.iconSize(1f),
                PropertyFactory.iconImage(expression),
                PropertyFactory.iconAllowOverlap(true),
                PropertyFactory.iconIgnorePlacement(true)
            )
        // 添加图层
        addLayer(layer)
    }

    fun addSource(source: Source) {
        mapLibreMap.style?.addSource(source)
    }

    fun addLayer(layer: Layer) {
        mapLibreMap.style?.addLayer(layer)
    }

    fun addImage(name: String, imageRes: Int) {
        val bitmap = drawableToBitmap(context, imageRes)
        mapLibreMap.style?.addImage(name, bitmap)
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