package space.think.cloud.cts.common.gis

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.maps.MapView
import space.think.cloud.cts.common.gis.runtime.rememberMapViewWithLifecycle

@Composable
fun MapLibreMapView(
    modifier: Modifier = Modifier,
    mapView: MapView = rememberMapViewWithLifecycle(),
) {
    AndroidView(factory = { mapView }, modifier = modifier)
}