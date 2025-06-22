package space.think.cloud.cts.lib.watermask

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import kotlin.math.max
import androidx.core.graphics.withRotation
import kotlin.math.ceil

object Watermark {

    fun addStyledTableWatermark(
        originalBitmap: Bitmap,
        tableData: Array<Array<String>>,
        maxLineLength: Int = 15, // 每行最大字符数
        lineSpace: Float = 20f,
        textColor: Int = Color.WHITE,
        textSize: Float = 50f,
        headerTitle: String? = null,
        headerColor: Int = Color.argb(200, 0, 0, 255),
        headerTextSize: Float = 56f,
        cellColor: Int = Color.argb(150, 0, 0, 0),
        borderColor: Int = Color.WHITE,
        borderWidth: Float = 2f,
        padding: Padding = Padding(20f),
        cellPadding: Padding = Padding(10f),
        position: Position = LeftBottom,
        rotation: Float = 0f // 旋转角度
    ): Bitmap {
        val mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)

        // 设置文字画笔
        val textPaint = Paint().apply {
            this.color = textColor
            this.textSize = textSize
            this.isAntiAlias = true
            this.textAlign = Paint.Align.LEFT
        }
        // 设置header文字画笔
        val headerPaint = Paint().apply {
            this.color = textColor
            this.textSize = headerTextSize
            this.typeface = Typeface.DEFAULT_BOLD
        }

        // 计算表格尺寸
        val rows = tableData.size
        val cols = tableData[0].size
        val cellWidths = FloatArray(cols)
        val rowHeights = FloatArray(rows + 1)
        val textBounds = Rect()

        // 表头宽度和高度
        var headerWidth = 0f
        var headerHeight = 0f

        if (headerTitle != null) {
            headerPaint.getTextBounds(headerTitle, 0, headerTitle.length, textBounds)
            headerWidth = headerPaint.measureText(headerTitle) + cellPadding.horizontal
            headerHeight = textBounds.height() + cellPadding.vertical * 2
        }

        // 预计算单元格尺寸
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                // 当前字符内容
                val currentContent = tableData[i][j]
                // 判断当前内容是否过长
                if (currentContent.length <= maxLineLength) {
                    textPaint.getTextBounds(currentContent, 0, currentContent.length, textBounds)
                    cellWidths[j] =
                        max(
                            cellWidths[j],
                            textPaint.measureText(currentContent) + cellPadding.horizontal * 2
                        )
                    rowHeights[i] =
                        max(rowHeights[i], textBounds.height().toFloat() + cellPadding.vertical * 2)
                } else {
                    // 计算字符串可以分多少行
                    val lines = ceil(currentContent.length / maxLineLength.toFloat()).toInt()
                    var lineMaxWidth = 0f
                    var lineMaxHeight = 0f
                    for (k in 0 until lines) {

                        val line =
                            if ((k + 1) * maxLineLength < currentContent.length) currentContent.substring(
                                k * maxLineLength,
                                (k + 1) * maxLineLength
                            )
                            else currentContent.substring(
                                k * maxLineLength,
                                currentContent.length - 1
                            )

                        textPaint.getTextBounds(
                            line,
                            0,
                            line.length,
                            textBounds
                        )
                        // 行宽
                        lineMaxWidth = max(
                            lineMaxWidth,
                            textPaint.measureText(line) + cellPadding.horizontal * 2
                        )
                        lineMaxHeight += textBounds.height().toFloat() + lineSpace
                    }
                    cellWidths[j] = lineMaxWidth
                    rowHeights[i] = lineMaxHeight + cellPadding.vertical * 2
                }
            }
        }
        // 表格的宽和高
        val tableWidth = max(cellWidths.sum(), headerWidth)
        val tableHeight = rowHeights.sum() + headerHeight

        // 计算表格位置
        val currentPosition =
            calculatingPosition(originalBitmap, padding, position, tableWidth, tableHeight)
        val tableLeft = currentPosition.first
        val tableTop = currentPosition.second

        // 应用旋转
        canvas.withRotation(
            rotation,
            tableLeft,
            tableTop
        ) {

            // 绘制表格
            var currentTop = tableTop
            val cellPaint = Paint().apply { isAntiAlias = true }
            val borderPaint = Paint().apply {
                color = borderColor
                style = Paint.Style.STROKE
                strokeWidth = borderWidth
                isAntiAlias = true
            }

            if (headerTitle != null) {
                // 表头颜色
                cellPaint.color = headerColor
                // 绘制单元格
                val headerRect = RectF(
                    tableLeft,
                    tableTop,
                    tableLeft + tableWidth,
                    currentTop + headerHeight
                )

                drawRect(headerRect, cellPaint)
                drawRect(headerRect, borderPaint)
                // 表头位置
                val headerX = tableLeft + tableWidth / 2 - headerWidth / 2
                val headerY =
                    tableTop + headerHeight / 2 - (headerPaint.ascent() + headerPaint.descent()) / 2
                // 绘制文字
                drawText(
                    headerTitle,
                    headerX,
                    headerY,
                    headerPaint
                )

                currentTop += headerHeight
            }

            for (i in 0 until rows) {
                var currentLeft = tableLeft

                for (j in 0 until cols) {
                    // 设置单元格样式
                    cellPaint.color = cellColor

                    // 绘制单元格
                    val cellRect = RectF(
                        currentLeft,
                        currentTop,
                        currentLeft + cellWidths[j],
                        currentTop + rowHeights[i]
                    )

                    drawRect(cellRect, cellPaint)
                    drawRect(cellRect, borderPaint)

                    // 绘制文字
                    val textX = currentLeft + cellPadding.horizontal

                    // 当前字符内容
                    val currentContent = tableData[i][j]
                    // 计算字符串可以分多少行
                    val lines = ceil(currentContent.length / maxLineLength.toFloat()).toInt()

                    for (k in 0 until lines) {
                        val line =
                            if ((k + 1) * maxLineLength < currentContent.length) currentContent.substring(
                                k * maxLineLength,
                                (k + 1) * maxLineLength
                            )
                            else currentContent.substring(
                                k * maxLineLength,
                                currentContent.length
                            )
                        // 每行的实际高度
                        val rowHeight =
                            (rowHeights[i] - cellPadding.vertical * 2 - (lines - 1) * lineSpace) / lines
                        // 字符串的高度
                        val fontHeight = textPaint.ascent() + textPaint.descent()
                        // 字符串的y位置
                        val textY =
                            currentTop +
                                    rowHeight * k +
                                    rowHeight * 1 / 2 -
                                    fontHeight / 2 + cellPadding.vertical + k * lineSpace
                        // 绘制文字
                        drawText(line, textX, textY, textPaint)
                    }
                    currentLeft += cellWidths[j]
                }

                currentTop += rowHeights[i]
            }

        }
        return mutableBitmap
    }

    private fun calculatingPosition(
        originalBitmap: Bitmap,
        padding: Padding,
        position: Position,
        tableWidth: Float,
        tableHeight: Float,
    ): Pair<Float, Float> {

        // 计算表格位置
        var tableLeft = 0f
        var tableTop = 0f
        when (position) {
            is LeftTop -> {
                tableLeft = padding.horizontal
                tableTop = padding.vertical
            }

            is RightTop -> {
                tableLeft = originalBitmap.width - tableWidth - padding.horizontal
                tableTop = padding.vertical
            }

            is LeftBottom -> {
                tableLeft = padding.horizontal
                tableTop = originalBitmap.height - tableHeight - padding.vertical
            }

            is RightBottom -> {
                tableLeft = originalBitmap.width - tableWidth - padding.horizontal
                tableTop = originalBitmap.height - tableHeight - padding.vertical
            }

            is TopCenter -> {
                tableLeft = (originalBitmap.width - tableWidth) / 2
                tableTop = padding.vertical
            }

            is BottomCenter -> {
                tableLeft = (originalBitmap.width - tableWidth) / 2
                tableTop = originalBitmap.height - tableHeight - padding.vertical
            }

            is LeftCenter -> {
                tableLeft = padding.horizontal
                tableTop = (originalBitmap.height - tableHeight) / 2
            }

            is RightCenter -> {
                tableLeft = originalBitmap.width - tableWidth - padding.horizontal
                tableTop = (originalBitmap.height - tableHeight) / 2
            }

            is Center -> {
                tableLeft = (originalBitmap.width - tableWidth) / 2
                tableTop = (originalBitmap.height - tableHeight) / 2
            }

            is CustomPosition -> {
                tableLeft = position.x
                tableTop = position.y
            }
        }

        return Pair(tableLeft, tableTop)


    }
}

class Padding(val horizontal: Float, val vertical: Float) {
    constructor(size: Float) : this(horizontal = size, vertical = size)
}

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
