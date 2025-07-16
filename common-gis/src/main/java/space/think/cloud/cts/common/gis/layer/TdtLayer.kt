package space.think.cloud.cts.common.gis.layer

import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.BackgroundLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.layers.RasterLayer
import space.think.cloud.cts.common.gis.source.tdt.TiandituManager

fun setupTiandituStyle(maplibreMap: MapLibreMap, key: String, callback: () -> Unit) {

    val tiandituManager = TiandituManager()
    // 影像图层源
    val tiandituSource = tiandituManager.createTdtSource(
        id = "tianditu-source",
        layer = "img_w",
        key = key
    )

    // 标注图层源
    val tiandituAnnotationSource = tiandituManager.createTdtSource(
        id = "tianditu-anno-source",
        layer = "cia_w",
        key = key
    )

    maplibreMap.setStyle(
        Style.Builder()
//            .withSource(GeoJsonSource("empty-source", FeatureCollection.fromFeatures(emptyList())))
            .withSource(tiandituSource)
            .withSource(tiandituAnnotationSource)
            .withLayer(
                BackgroundLayer("background-layer")
                    .withProperties(
                        PropertyFactory.backgroundColor("#00000000"), // 设置背景色
                        PropertyFactory.backgroundOpacity(1.0f)
                    )
            )
            .withLayer(
                RasterLayer("tianditu-layer", "tianditu-source")
            )
            .withLayer(
                RasterLayer("tianditu-anno-layer", "tianditu-anno-source")
//                    .withProperties(PropertyFactory.rasterOpacity(0.3f))
            )
    ) {
        callback()
    }

}