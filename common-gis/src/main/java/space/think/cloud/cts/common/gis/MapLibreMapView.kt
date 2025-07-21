package space.think.cloud.cts.common.gis

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.Marker
import org.maplibre.android.maps.MapLibreMapOptions
import org.maplibre.android.maps.MapView

@Composable
fun MapLibreMapView(
    modifier: Modifier = Modifier,
    onInfoWindowClick: (Marker) -> Unit,
    onMapReady: (MapLibreMapController) -> Unit
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val mapView = rememberMapView()

    DisposableEffect(lifecycle, mapView) {
        val observer = MapLibreLifecycleObserver(mapView)
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    val context = LocalContext.current

    AndroidView(
        factory = { mapView },
        modifier = modifier,
        update = { view ->
            view.getMapAsync { map ->
                val mapController = MapLibreMapController(context, map)
                // 添加infoWindow 点击事件
                mapController.onInfoWindowClickListener {
                    onInfoWindowClick(it)
                }
                // 回调map对象
                onMapReady(mapController)
            }
        }
    )
}

@Composable
fun rememberMapView(): MapView {
    val context = LocalContext.current
    // 初始化 MapLibre
    remember { MapLibre.getInstance(context) }
    return remember {
        var mapLibreMapOptions = MapLibreMapOptions.createFromAttributes(context).apply {
            logoEnabled(false)
            attributionEnabled(false)

        }
        MapView(context, mapLibreMapOptions).apply {
            tag = false
            onCreate(null)
        }
    }
}