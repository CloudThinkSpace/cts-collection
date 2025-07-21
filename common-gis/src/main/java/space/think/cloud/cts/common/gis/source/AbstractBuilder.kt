package space.think.cloud.cts.common.gis.source

abstract class AbstractBuilder: Builder {
    protected var id: String = ""
    protected var uri: String = ""
    protected var tileSize: Int = 256
    protected var minMapZoom: Float = 0f
    protected var maxMapZoom: Float = 22f

    open fun withId(id: String): AbstractBuilder {
        this.id = id
        return this
    }

    fun withUri(uri: String): AbstractBuilder {
        this.uri = uri
        return this
    }

    open fun withTileSize(tileSize: Int): AbstractBuilder {
        this.tileSize = tileSize
        return this
    }

    fun withMinZoom(minZoom: Float): AbstractBuilder {
        this.minMapZoom = minZoom
        return this
    }

    fun withMaxZoom(maxZoom: Float): AbstractBuilder {
        this.maxMapZoom = maxZoom
        return this
    }
}