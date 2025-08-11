package space.think.cloud.cts.common.gis.geocode

import com.google.gson.annotations.SerializedName

data class GeoCodeResult(
    val msg: String,
    val result: GeoCodeData?,
)

data class GeoCodeData(
    @SerializedName("formatted_address")
    val formattedAddress: String,
    val location: GeoCodeLocation,
)

data class GeoCodeLocation(
    val lon: Double,
    val lat: Double,
)