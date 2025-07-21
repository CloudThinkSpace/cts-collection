package space.think.cloud.cts.common.gis.source.tdt

import org.maplibre.android.style.sources.RasterSource
import org.maplibre.android.style.sources.Source
import org.maplibre.android.style.sources.TileSet
import space.think.cloud.cts.common.gis.source.AbstractBuilder

class TdtSourceBuilder : AbstractBuilder() {

    private var baseUrl: String? = null
    private var layer: String = ""
    private var key: String? = null

    override fun withId(id: String): TdtSourceBuilder {
        this.id = id
        return this
    }

    fun withBaseUrl(baseUrl: String): TdtSourceBuilder {
        this.baseUrl = baseUrl
        return this
    }

    fun withLayer(layer: String): TdtSourceBuilder {
        this.layer = layer
        return this
    }

    fun withKey(key: String): TdtSourceBuilder {
        this.key = key
        return this
    }

    override fun withTileSize(tileSize: Int): TdtSourceBuilder {
        this.tileSize = tileSize
        return this
    }

    override fun build(): Source {

        // 天地图子域名数组
        val subdomains = arrayOf("0", "1", "2", "3", "4", "5", "6", "7")

        val newBaseUrl = if (baseUrl == null) {
            // 随机选择一个子域名（或实现轮询逻辑）
            val subdomain = subdomains.random()
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