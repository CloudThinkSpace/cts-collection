package space.think.cloud.cts.common.gis

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import kotlinx.coroutines.flow.first
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.style.expressions.Expression
import org.maplibre.geojson.Feature
import space.think.cloud.cts.common.gis.MapLibreManager.Companion.DEFAULT_ZOOM
import space.think.cloud.cts.common.gis.layer.addTdtLayers
import space.think.cloud.cts.common_datastore.DataStoreUtil
import space.think.cloud.cts.common_datastore.PreferencesKeys

@SuppressLint("MissingPermission")
class MapLibreMapController(
    val context: Context,
    mapView: MapView,
    maplibreMap: MapLibreMap
) {

    companion object {
        // 天地图token
        const val TDT_TOKEN = "e6ed3fdaf6ca24a041d8dcb69ab279f2"
        const val DEFAULT_MARKER_SYMBOL_NAME = "cts-marker"
    }

    // 被选中的点位
    private var selectCtsMarker: CtsMarker? = null

    // 地图管理器
    private val mapLibreManager = MapLibreManager(context, mapView, maplibreMap)

    private val dataStoreUtil = DataStoreUtil(context)

    // 全图范围
    private var latLngBounds: LatLngBounds? = null

    init {
        mapLibreManager.onStyle { style ->
            // 初始化天地图
            addTdtLayers(style, TDT_TOKEN)
        }
        // 显示定位
        mapLibreManager.setLocationEnable(true)
        // 添加地图点击事件
        mapLibreManager.addMapClickListener { features ->
            handleMapClick(features)
        }
        // 添加地图移动事件
        mapLibreManager.addMoveListener {
            selectCtsMarker?.let {
                mapLibreManager.updateInfoWindow(it)
            }
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
     * 设置选择的要素
     */
    fun setSelectCtsMarker(marker: CtsMarker) {
        this.selectCtsMarker = marker
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
            // 1. 获取第一个要素的属性
            val feature = features[0]
            val properties = feature.properties()

            properties?.let {
                selectCtsMarker = CtsMarker.fromJson(it).apply {
                    if (mapLibreManager.getCurrentZoom() >= DEFAULT_ZOOM) {
                        showInfoWindow(this)
                    } else {
                        mapLibreManager.animateToLatLng(LatLng(this.lat, this.lon)) {
                            showInfoWindow(this)
                        }
                    }
                }
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
        name: String = DEFAULT_MARKER_SYMBOL_NAME,
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
        mapLibreManager.addOnInfoWindowListener {
            selectCtsMarker?.let {
                onClick(it)
            }
        }
    }

    fun toggleLayer(layerId: String, visible: Boolean) {
        mapLibreManager.setRasterLayerVisible(layerId, visible)
    }

    fun animateToLatLng(
        latLng: LatLng,
        zoom: Double = DEFAULT_ZOOM,
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
    fun showInfoWindow(ctsMarker: CtsMarker) {
        mapLibreManager.showInfoWindow(
            LatLng(ctsMarker.lat, ctsMarker.lon),
            ctsMarker.title,
            ctsMarker.lon.toString(),
            ctsMarker.lat.toString()
        )
    }

    // 隐藏InfoWindow
    fun hideInfoWindow() {
        mapLibreManager.hideInfoWindow()
    }

}