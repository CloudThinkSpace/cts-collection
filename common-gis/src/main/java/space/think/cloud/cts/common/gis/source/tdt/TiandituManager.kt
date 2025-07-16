package space.think.cloud.cts.common.gis.source.tdt

import org.maplibre.android.style.sources.RasterSource
import org.maplibre.android.style.sources.TileSet

// 实现子域名轮询
class TiandituManager {
    private val subdomains = listOf("0", "1", "2", "3", "4", "5", "6", "7")
    private var currentIndex = 0

    fun getNextSubdomain(): String {
        currentIndex = (currentIndex + 1) % subdomains.size
        return subdomains[currentIndex]
    }

    // c
    fun createTdtSource(
        id: String,
        baseUrl: String? = null,
        layer: String,
        key: String? = null,
        tileSize: Int = 256
    ): RasterSource {

        val newBaseUrl = if (baseUrl == null) {
            val subdomain = getNextSubdomain()
            "http://t${subdomain}.tianditu.gov.cn"
        } else baseUrl

        val template = "${newBaseUrl}/${layer}/wmts?" +
                "SERVICE=WMTS&" +
                "REQUEST=GetTile&" +
                "VERSION=1.0.0&" +
                "LAYER=${layer.substring(0, 3)}&" +
                "STYLE=default&" +
                "TILEMATRIXSET=w&" +
                "FORMAT=tiles&" +
                "TILEMATRIX={z}&" +
                "TILEROW={y}&" +
                "TILECOL={x}" +
                (key?.let { "&tk=$it" } ?: "")

        return RasterSource(
            id,
            TileSet("2.1.0", template).apply {
                scheme = "xyz"
                minZoom = 0f
                maxZoom = 22f
            },
            tileSize
        )
    }
}