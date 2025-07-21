package space.think.cloud.cts.common.gis.source.cts

import org.maplibre.android.style.sources.RasterSource
import org.maplibre.android.style.sources.Source
import org.maplibre.android.style.sources.TileSet
import space.think.cloud.cts.common.gis.source.AbstractBuilder

class RasterSourceBuilder : AbstractBuilder() {

    override fun build(): Source {

        return RasterSource(
            id,
            TileSet("2.1.0", uri).apply {
                scheme = "xyz"
                minZoom = minMapZoom
                maxZoom = maxMapZoom
            },
            tileSize
        )
    }
}