package space.think.cloud.cts.common.gis.layer

import androidx.compose.runtime.MutableState

data class Layer(
    val name: String,
    val type: String,
    val format: String,
    val url: String,
    var checked: MutableState<Boolean>
)