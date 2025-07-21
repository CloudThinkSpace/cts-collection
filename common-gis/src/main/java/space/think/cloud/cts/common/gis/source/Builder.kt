package space.think.cloud.cts.common.gis.source

import org.maplibre.android.style.sources.Source

interface Builder {
    fun build(): Source
}