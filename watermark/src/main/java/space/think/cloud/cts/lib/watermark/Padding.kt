package space.think.cloud.cts.lib.watermark

data class Padding(val left:Float, val top:Float, val right:Float, val bottom:Float) {
    constructor(size: Float) : this(horizontal = size, vertical = size)
    constructor(horizontal: Float, vertical: Float) : this(left = horizontal, top = vertical, right = horizontal, bottom = vertical)
}