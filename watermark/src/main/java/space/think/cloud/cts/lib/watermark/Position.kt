package space.think.cloud.cts.lib.watermark

sealed class Position
data object LeftTop : Position()
data object RightTop : Position()
data object LeftBottom : Position()
data object RightBottom : Position()
data object TopCenter : Position()
data object BottomCenter : Position()
data object LeftCenter : Position()
data object RightCenter : Position()
data object Center : Position()
data class CustomPosition(val x: Float, val y: Float) : Position()
