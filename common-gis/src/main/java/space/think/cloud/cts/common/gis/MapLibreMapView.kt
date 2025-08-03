package space.think.cloud.cts.common.gis

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import space.think.cloud.cts.common.gis.runtime.awaitMap
import space.think.cloud.cts.common.gis.runtime.rememberMapViewWithLifecycle

@Composable
fun MapLibreMapView(
    modifier: Modifier = Modifier,
    onInfoWindowClick: (CtsMarker) -> Unit,
    onMapReady: (MapLibreMapController) -> Unit
) {
    val context = LocalContext.current
    val mapView = rememberMapViewWithLifecycle()

    AndroidView(factory = { mapView }, modifier = modifier)

    LaunchedEffect(Unit) {
        val maplibreMap = mapView.awaitMap()
        val mapController = MapLibreMapController(context, maplibreMap)
        // 添加infoWindow 点击事件
        mapController.onInfoWindowClickListener {
            onInfoWindowClick(it)
        }
        // 回调map对象
        onMapReady(mapController)
    }
}