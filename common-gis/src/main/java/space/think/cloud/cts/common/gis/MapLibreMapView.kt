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
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapLibreMapOptions
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.BackgroundLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.FeatureCollection
import space.think.cloud.cts.common.gis.layer.setupTiandituStyle

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

                map.onInfoWindowClickListener = object : MapLibreMap.OnInfoWindowClickListener {
                    override fun onInfoWindowClick(marker: Marker): Boolean {
                        onInfoWindowClick(marker)
                        return true
                    }

                }

                setupTiandituStyle(map, "e6ed3fdaf6ca24a041d8dcb69ab279f2") {
                    val mapController = MapLibreMapController(context, map)
                    onMapReady(mapController)
                }

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