package space.think.cloud.cts.lib.watermark

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF

data class RoundedCornerShape(
    private val topStart: Float = 0f,
    private val topEnd: Float = 0f,
    private val bottomEnd: Float = 0f,
    private val bottomStart: Float = 0f,
) {
    constructor(size: Float) : this(
        topStart = size,
        topEnd = size,
        bottomStart = size,
        bottomEnd = size
    )

    constructor(top: Float, bottom: Float) : this(
        topStart = top,
        topEnd = top,
        bottomStart = bottom,
        bottomEnd = bottom
    )

    fun draw(
        canvas: Canvas,
        color: Int,
        rect: RectF,
        style: Paint.Style = Paint.Style.FILL,
        borderWidth: Float = 2f
    ) {
        // 画笔
        val paint = Paint()
        paint.color = color
        paint.style = style
        paint.isAntiAlias = true
        if (style == Paint.Style.STROKE) {
            paint.strokeWidth = borderWidth
        }

        val path = Path()
        // 从右下角圆角开始
        path.moveTo(rect.right, rect.bottom - bottomEnd)

        if (bottomEnd > 0) {
            // 绘制右下圆角
            path.quadTo(
                rect.right, rect.bottom,
                rect.right - bottomEnd, rect.bottom
            )
        }

        // 绘制左下边
        path.lineTo(rect.left + bottomStart, rect.bottom)

        if (bottomStart > 0) {
            // 绘制左下角圆角
            path.quadTo(
                rect.left, rect.bottom,
                rect.left, rect.bottom - bottomStart
            )
        }

        path.lineTo(rect.left, rect.top + topStart) // 左边

        if (topStart > 0) {
            // 绘制左上圆角
            path.quadTo(
                rect.left, rect.top,
                rect.left + topStart, rect.top
            )
        }

        path.lineTo(rect.right - topEnd, rect.top) // 右上角

        if (topEnd > 0) {
            // 绘制右上圆角
            path.quadTo(
                rect.right, rect.top,
                rect.right, rect.top + topEnd
            )
        }

        path.lineTo(rect.right, rect.bottom - bottomEnd) // 右边

        canvas.drawPath(path, paint)
    }

}