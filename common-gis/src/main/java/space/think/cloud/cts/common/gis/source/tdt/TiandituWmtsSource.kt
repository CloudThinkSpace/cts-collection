package space.think.cloud.cts.common.gis.source.tdt

import org.maplibre.android.style.sources.RasterSource
import org.maplibre.android.style.sources.TileSet

// c
fun createTdtSource(
    id: String,
    baseUrl: String? = null,
    layer: String,
    key: String? = null,
    tileSize: Int = 256
): RasterSource {

    val newBaseUrl = if (baseUrl == null) {
        // 天地图子域名数组
        val subdomains = arrayOf("0", "1", "2", "3", "4", "5", "6", "7")
        // 随机选择一个子域名（或实现轮询逻辑）
        val subdomain = subdomains.random()

        "http://t${subdomain}.tianditu.gov.cn"
    } else baseUrl

    val template = "${newBaseUrl}/${layer}/wmts?" +
            "SERVICE=WMTS&" +
            "REQUEST=GetTile&" +
            "VERSION=1.0.0&" +
            "LAYER=img&" +
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
            maxZoom = 18f
        },
        tileSize
    )
}