package space.think.cloud.cts.common.gis.layer

import android.content.Context
import android.graphics.BitmapFactory
import org.maplibre.android.maps.Style
import space.think.cloud.cts.common.gis.R

class LocationLayer(context: Context, style: Style) {

    init {
        // 添加定位图标
        style.addImage("cts-location-image", BitmapFactory.decodeResource(context.resources, R.drawable.location_blue))

    }

}