package space.think.cloud.cts.common.gis

import androidx.annotation.DrawableRes

data class CtsMarker(
    val lon: Double,
    val lat: Double,
    val title: String,
    val description: String,
    @DrawableRes val icon: Int,
)