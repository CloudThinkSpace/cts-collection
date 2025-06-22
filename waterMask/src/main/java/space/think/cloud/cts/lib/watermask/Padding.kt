package space.think.cloud.cts.lib.watermask

data class Padding(val horizontal: Float, val vertical: Float) {
    constructor(size: Float) : this(horizontal = size, vertical = size)
}