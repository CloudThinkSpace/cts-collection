package space.think.cloud.cts.common.gis.layer

import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.BackgroundLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.layers.RasterLayer
import space.think.cloud.cts.common.gis.source.tdt.TdtSourceBuilder

fun setupTiandituStyle(maplibreMap: MapLibreMap, key: String) {

    // 影像图层源
    val tiandituSource = TdtSourceBuilder()
        .withId("tianditu-source")
        .withLayer("img_w")
        .withKey(key)
        .build()

    val tiandituAnnotationSource = TdtSourceBuilder()
        .withId("tianditu-anno-source")
        .withLayer("cia_w")
        .withKey(key)
        .build()

    maplibreMap.setStyle(
        Style.Builder()
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
            )
    ) {
    }

}