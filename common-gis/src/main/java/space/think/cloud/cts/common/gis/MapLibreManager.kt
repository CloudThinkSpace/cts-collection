package space.think.cloud.cts.common.gis

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import android.widget.TextView
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.location.LocationComponentActivationOptions
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapLibreMap.CancelableCallback
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.expressions.Expression
import org.maplibre.android.style.layers.BackgroundLayer
import org.maplibre.android.style.layers.Layer
import org.maplibre.android.style.layers.Property
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.layers.RasterLayer
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.android.style.sources.Source
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
import space.think.cloud.cts.common.gis.source.cts.RasterSourceBuilder
import space.think.cloud.cts.common.gis.utils.DpUtils


class MapLibreManager(
    private val context: Context,
    private val mapView: MapView,
    private val maplibreMap: MapLibreMap,
    private val backgroundColor: String = "#FFFFFF",
    private val backgroundOpacity: Float = 1.0f,
) : MapLibreMap.OnMapClickListener, MapLibreMap.OnCameraMoveListener,
    MapLibreMap.OnCameraIdleListener {


    companion object {

        const val BACKGROUND_LAYER_NAME = "cts_background-layer"
        const val DEFAULT_ZOOM = 14.0

        const val RASTER_LAYER = "-raster-layer"
        const val RASTER_SOURCE = "-raster-source"

        const val SYMBOL_LAYER = "-symbol-layer"
        const val SYMBOL_SOURCE = "-symbol-source"
    }


    // 地图style
    private lateinit var style: Style

    // 待查询的图层列表
    private val layerIds: MutableList<String> = mutableListOf()
    private var mapListener: ((List<Feature>) -> Unit)? = null
    private var moveListener: (() -> Unit)? = null
    private var onInfoWindowClickListener: (() -> Unit)? = null

    // 当前的窗体
    private var currentInfoWindow: View? = null

    /**
     * 初始化函数
     */
    init {
        // 初始化style
        initStyle()
        // 添加地图点击事件
        maplibreMap.addOnMapClickListener(this)
        maplibreMap.addOnCameraMoveListener(this)
        maplibreMap.addOnCameraIdleListener(this)
        // 设置最大缩放级别
        maplibreMap.setMaxZoomPreference(17.0)
    }

    /**
     * 刷新窗体位置
     */
    fun updateInfoWindow(ctsMarker: CtsMarker) {
        // 计算屏幕位置
        val point = maplibreMap.projection.toScreenLocation(LatLng(ctsMarker.lat, ctsMarker.lon))
        currentInfoWindow?.let {
            currentInfoWindow?.x = point.x.minus(it.width / 2)
            currentInfoWindow?.y =
                point.y.minus(it.height).minus(DpUtils.dpToPx(context, 8f)) // 显示在Marker上方
        }
    }

    /**
     * 设置查询图层列表
     * @param layerIds 图层列表
     */
    fun setQueryLayer(layerIds: List<String>) {
        this.layerIds.apply {
            clear()
            addAll(layerIds)
        }
    }

    /**
     * 添加地图点击事件，返回被点击的要素列表
     * @param listener 监听接口
     */
    fun addMapClickListener(listener: (List<Feature>) -> Unit) {
        mapListener = listener
    }

    /**
     * 添加地图移动监听事件
     */
    fun addMoveListener(listener: () -> Unit) {
        moveListener = listener
    }

    /**
     * 添加infoWindow 点击事件
     */
    fun addOnInfoWindowListener(listener: () -> Unit) {
        onInfoWindowClickListener = listener
    }

    fun getCurrentZoom(): Double {
        return maplibreMap.cameraPosition.zoom
    }

    /**
     * 初始化style，默认添加背景图层
     */
    private fun initStyle() {
        maplibreMap.setStyle(
            Style.Builder()
                .withLayer(
                    BackgroundLayer(BACKGROUND_LAYER_NAME)
                        .withProperties(
                            PropertyFactory.backgroundColor(backgroundColor), // 设置背景色
                            PropertyFactory.backgroundOpacity(backgroundOpacity)
                        )
                )
        ) {
            style = it
        }
    }

    /**
     * 获取地图style
     */
    @Suppress("unused")
    fun getStyle(): Style {
        return style
    }

    /**
     * 加载style完成后，回调函数
     */
    fun onStyle(callback: (Style) -> Unit) {
        callback.invoke(style)
    }

    /**
     * 设置是否显示定位图标
     * @param enable 是否显示定位图标
     */
    @SuppressLint("MissingPermission")
    fun setLocationEnable(enable: Boolean) {
        val locationComponent = maplibreMap.locationComponent
        locationComponent.activateLocationComponent(
            LocationComponentActivationOptions.builder(context, style).build()
        )
        locationComponent.isLocationComponentEnabled = enable
    }

    /**
     * 平移到指定坐标，带动画效果
     * @param latLng 坐标
     * @param zoom 缩放级别
     * @param delay 动画运行时间
     * @param onFinish 执行完成后执行的函数
     */
    fun animateToLatLng(
        latLng: LatLng,
        zoom: Double = DEFAULT_ZOOM,
        delay: Int = 2000,
        onFinish: (() -> Unit)? = null
    ) {
        // 移动到点位
        val cameraPosition = CameraPosition.Builder()
            .target(latLng)
            .zoom(zoom)
            .build()

        maplibreMap.animateCamera(
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

    /**
     * 平移到指定坐标，带动画效果
     * @param bounds 缩放范围坐标列表
     * @param delay 动画运行时间
     * @param onFinish 执行完成后执行的函数
     */
    fun animateToBounds(
        bounds: List<LatLng>,
        delay: Int = 2000,
        onFinish: (() -> Unit)? = null
    ) {
        val latLngBounds = LatLngBounds.fromLatLngs(bounds)
        maplibreMap.getCameraForLatLngBounds(latLngBounds)?.let {
            val newCameraPosition = CameraPosition.Builder()
                .target(it.target)
                .zoom(it.zoom - 0.5)
                .build()
            maplibreMap.animateCamera(
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

    /**
     * 平移到指定坐标，带动画效果
     * @param latLngBounds 缩放范围
     * @param delay 动画运行时间
     * @param onFinish 执行完成后执行的函数
     */
    fun animateToBounds(
        latLngBounds: LatLngBounds,
        delay: Int = 2000,
        onFinish: (() -> Unit)? = null
    ) {
        maplibreMap.getCameraForLatLngBounds(latLngBounds)?.let {
            val newCameraPosition = CameraPosition.Builder()
                .target(it.target)
                .zoom(it.zoom - 0.5)
                .build()
            maplibreMap.animateCamera(
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

    /**
     * 设置图层是否可见
     * @param layerId 图层
     * @param visible 是否可见
     *
     * */
    fun setLayerVisible(layerId: String, visible: Boolean) {
        val layer = style.getLayer(layerId)
        layer?.setVisible(visible)
    }

    /**
     * 设置Raster图层是否可见
     */
    fun setRasterLayerVisible(layerId: String, visible: Boolean) {
        val newLayerId = "$layerId$RASTER_LAYER"
        setLayerVisible(newLayerId, visible)
    }

    /**
     *  设置图层是否可见
     *  @param visible 是否可见
     */
    fun Layer.setVisible(visible: Boolean) {
        setProperties(
            PropertyFactory.visibility(
                if (visible) Property.VISIBLE else Property.NONE
            )
        )
    }

    /**
     * 添加数据源
     * @param source 数据源
     */
    fun addSource(source: Source) {
        style.addSource(source)
    }

    /**
     * 添加图层
     * @param layer
     */
    fun addLayer(layer: Layer) {
        style.addLayer(layer)
    }

    /**
     * 添加图片图标
     * @param imageName 图标名称
     * @param bitmap 图标
     */
    fun addImage(imageName: String, bitmap: Bitmap) {
        style.addImage(imageName, bitmap)
    }

    /**
     * 添加图片图标
     * @param imageName 图标名称
     * @param drawableResId 图标
     */
    @Suppress("unused")
    fun addImage(imageName: String, drawableResId: Int) {
        val bitmap = BitmapFactory.decodeResource(context.resources, drawableResId)
        addImage(imageName, bitmap)
    }

    /**
     * 添加栅格图层
     * @param name 数据源和图层名称
     * @param uri 在线图层的uri链接
     */
    fun addRasterLayer(name: String, uri: String) {

        val rasterLayerId = "$name$RASTER_LAYER"
        val rasterSourceId = "$name$RASTER_SOURCE"

        val rasterSource = RasterSourceBuilder()
            .withId(rasterSourceId)
            .withUri(Uri.decode(uri))
            .build()
        // 移除图层和数据源
        removeLayer(rasterLayerId)
        removeSource(rasterSourceId)
        // 添加数据
        style.addSource(rasterSource)
        style.addLayer(RasterLayer(rasterLayerId, rasterSourceId))
    }

    /**
     * 添加标记图层
     * @param name 名称
     * @param expression 表达式
     * @param features 要素列表
     * @param isZoom 是否缩放到数据范围
     * @param onFinish 缩放完成后操作函数
     */
    fun addSymbolLayer(
        name: String,
        expression: Expression,
        features: List<Feature>,
        isZoom: Boolean = false,
        onBounds: ((LatLngBounds) -> Unit)? = null,
        onFinish: (() -> Unit)? = null
    ) {
        val layerName = "$name$SYMBOL_LAYER"
        val sourceName = "$name$SYMBOL_SOURCE"
        // 删除图层
        removeLayer(layerName)
        // 构造geoJson数据源
        val source = GeoJsonSource(sourceName, FeatureCollection.fromFeatures(features))
        // 移除已经存在的数据源
        removeSource(sourceName)
        // 添加数据源
        addSource(source)
        // 创建样式图层
        val layer = SymbolLayer(layerName, sourceName)
            .withProperties(
                PropertyFactory.iconSize(1f),
                PropertyFactory.iconImage(expression),
                PropertyFactory.iconAllowOverlap(true),
                PropertyFactory.iconIgnorePlacement(true)
            )
        // 添加图层
        addLayer(layer)
        // 设置查询图层
        setQueryLayer(listOf(layerName))
        // 获取要素的所有坐标
        val data = features.map {
            val geometry = it.geometry()
            when (geometry) {
                is Point -> {
                    LatLng(geometry.latitude(), geometry.longitude())
                }

                else -> {
                    throw IllegalArgumentException("The geometry of feature is not an Point type")
                }
            }
        }
        // 图层范围
        val latLngBounds = LatLngBounds.fromLatLngs(data)
        onBounds?.invoke(latLngBounds)
        // 判断是否需要缩放移动
        if (isZoom) {
            // 移动到指定访问内
            animateToBounds(latLngBounds, onFinish = onFinish)
        }
    }

    /**
     * 删除图层
     * @param layerId 图层编号
     */
    fun removeLayer(layerId: String) {
        val layer = style.getLayer(layerId)
        layer?.let {
            style.removeLayer(layerId)
        }
    }

    /**
     * 删除图层
     * @param layer 图层
     */
    @Suppress("unused")
    fun removeLayer(layer: Layer) {
        style.removeLayer(layer)
    }

    /**
     * 删除数据源
     * @param sourceId 数据源编号
     */
    fun removeSource(sourceId: String) {
        val source = style.getSource(sourceId)
        source?.let {
            style.removeSource(sourceId)
        }
    }

    /**
     * 删除数据源
     * @param source 数据源
     */
    @Suppress("unused")
    fun removeSource(source: Source) {
        style.removeSource(source)
    }

    /**
     * 展示infoWindow
     */
    fun showInfoWindow(latLng: LatLng, title: String, lon: String, lat: String) {

        currentInfoWindow?.let {
            mapView.removeView(it)
        }
        // 创建信息窗口视图
        currentInfoWindow =
            LayoutInflater.from(context).inflate(R.layout.common_gis_info_window, mapView, false)
        currentInfoWindow?.findViewById<TextView>(R.id.title)?.text = title
        currentInfoWindow?.findViewById<TextView>(R.id.longitude)?.text = lon
        currentInfoWindow?.findViewById<TextView>(R.id.latitude)?.text = lat

        currentInfoWindow?.visibility = View.INVISIBLE
        // 添加到地图并显示
        mapView.addView(currentInfoWindow)

        currentInfoWindow?.viewTreeObserver?.addOnGlobalLayoutListener(
            object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    currentInfoWindow?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                    // 计算屏幕位置
                    val point = maplibreMap.projection.toScreenLocation(latLng)
                    currentInfoWindow?.let {
                        currentInfoWindow?.x = point.x.minus(it.width / 2)
                        currentInfoWindow?.y = point.y.minus(it.height)
                            .minus(DpUtils.dpToPx(context, 8f)) // 显示在Marker上方
                    }
                    currentInfoWindow?.setOnClickListener {
                        onInfoWindowClickListener?.invoke()
                    }
                    currentInfoWindow?.visibility = View.VISIBLE

                }
            }
        )
    }

    fun hideInfoWindow() {
        currentInfoWindow?.let {
            mapView.removeView(it)
        }
    }

    override fun onMapClick(latlng: LatLng): Boolean {
        // 将坐标转换成屏幕坐标
        val clickPoint = maplibreMap.projection.toScreenLocation(latlng)
        // 获取点击位置的要素
        val features =
            maplibreMap.queryRenderedFeatures(clickPoint, *layerIds.toTypedArray())
        mapListener?.invoke(features)
        return true
    }

    override fun onCameraMove() {
        moveListener?.invoke()
    }

    override fun onCameraIdle() {
        moveListener?.invoke()
    }
}