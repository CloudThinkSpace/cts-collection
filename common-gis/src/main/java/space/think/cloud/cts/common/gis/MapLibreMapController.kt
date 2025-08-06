package space.think.cloud.cts.common.gis

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.flow.first
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.style.expressions.Expression
import org.maplibre.geojson.Feature
import org.maplibre.geojson.Point
import space.think.cloud.cts.common.gis.layer.addTdtLayers
import space.think.cloud.cts.common_datastore.DataStoreUtil
import space.think.cloud.cts.common_datastore.PreferencesKeys

@SuppressLint("MissingPermission")
class MapLibreMapController(
    val context: Context,
    val maplibreMap: MapLibreMap
) {

    // 默认点位图层名
    private val defaultMarkerSymbolName = "cts-marker"

    // 被选中的图标
    private var selectMarker: CtsMarker? = null

    // 地图管理器
    private val mapLibreManager = MapLibreManager(context, maplibreMap)

    private val dataStoreUtil = DataStoreUtil(context)

    // 全图范围
    private var latLngBounds: LatLngBounds? = null

    init {
        mapLibreManager.onStyle { style ->
            // 初始化天地图
            addTdtLayers(style, "e6ed3fdaf6ca24a041d8dcb69ab279f2")
        }
        // 显示定位
        mapLibreManager.setLocationEnable(true)
        // 添加地图点击事件
        mapLibreManager.addMapClickListener { features ->
            handleMapClick(features)
        }
    }

    /**
     * 全图
     */
    fun goFullBounds() {
        latLngBounds?.let {
            mapLibreManager.animateToBounds(it)
        }
    }

    /**
     * 当前位置
     */
    suspend fun goLocation() {
        val lat = dataStoreUtil.getData(PreferencesKeys.LAT_KEY, 0.0).first()
        val lon = dataStoreUtil.getData(PreferencesKeys.LON_KEY, 0.0).first()
        mapLibreManager.animateToLatLng(LatLng(lat, lon))
    }

    private fun handleMapClick(features: List<Feature>) {

        if (features.isNotEmpty()) {
            // 2. 获取第一个要素的属性
            val feature = features[0]
            val properties = feature.properties()
            val title = properties?.get("code")?.asString ?: "无标题"
            val taskId = properties?.get("taskId")?.asString ?: ""

            // 3. 获取要素的几何位置
            val point = feature.geometry() as Point
            val latLng = LatLng(point.latitude(), point.longitude())

            // 4. 显示自定义InfoWindow
            selectMarker = CtsMarker(
                taskId = taskId,
                lon = latLng.longitude,
                lat = latLng.latitude,
                title = title,
                description = "",
                icon = R.drawable.location_blue
            ).apply {
                showInfoWindow(this)
            }

        } else {
            // 点击了非标记区域，隐藏InfoWindow
            hideInfoWindow()
        }
    }

    fun addLayer(name: String, uri: String) {
        mapLibreManager.addRasterLayer(name, uri)
    }

    fun addSymbolLayer(
        name: String = defaultMarkerSymbolName,
        expression: Expression,
        features: List<Feature>
    ) {

        mapLibreManager.addSymbolLayer(
            name = name,
            expression = expression,
            features = features,
            onBounds = {
                latLngBounds = it
            }
        )
    }

    fun addImage(name: String, bitmap: Bitmap) {
        mapLibreManager.addImage(name, bitmap)
    }

    fun onInfoWindowClickListener(onClick: (CtsMarker) -> Unit) {
        maplibreMap.onInfoWindowClickListener = object : MapLibreMap.OnInfoWindowClickListener {
            override fun onInfoWindowClick(marker: Marker): Boolean {
                selectMarker?.let {
                    onClick(it)
                }
                return true
            }
        }
    }

    fun toggleLayer(layerId: String, visible: Boolean) {
        mapLibreManager.setLayerVisible(layerId, visible)
    }

    fun animateToLatLng(
        latLng: LatLng,
        zoom: Double = 10.0,
        delay: Int = 2000,
        onFinish: (() -> Unit)? = null
    ) {
        mapLibreManager.animateToLatLng(latLng, zoom, delay, onFinish)
    }

    fun animateToBounds(
        bounds: List<LatLng>,
        delay: Int = 2000,
        onFinish: (() -> Unit)? = null
    ) {
        mapLibreManager.animateToBounds(bounds, delay) {
            onFinish?.invoke()
        }
    }

    // 显示InfoWindow
    fun showInfoWindow(marker: CtsMarker) {
        selectMarker = marker
        val markerOptions = MarkerOptions().apply {
            position = LatLng(marker.lat, marker.lon)
            title = marker.title
        }
        maplibreMap.selectMarker(Marker(markerOptions))
    }

    // 显示InfoWindow
    fun showInfoWindow(marker: Marker) {
        maplibreMap.selectMarker(marker)
    }

    // 隐藏InfoWindow
    fun hideInfoWindow() {
        maplibreMap.deselectMarkers()
    }

}