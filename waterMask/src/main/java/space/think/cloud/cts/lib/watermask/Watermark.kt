package space.think.cloud.cts.lib.watermask

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import androidx.core.graphics.withRotation
import kotlin.math.ceil
import kotlin.math.max


object Watermark {

    fun addStyledTableWatermark(
        originalBitmap: Bitmap,
        tableData: Array<Array<String>>,
        maxLineLength: Int = 20, // 每行最大字符数
        lineSpace: Float = 20f,
        textColor: Int = Color.WHITE,
        textSize: Float = 60f,
        headerTitle: String? = null,
        headerColor: Int = Color.argb(200, 0, 0, 255),
        headerTextSize: Float = 86f,
        headerTextColor: Int = Color.WHITE,
        headerPadding: Padding = Padding(10f, 50f),
        cellColor: Int = Color.argb(150, 0, 0, 0),
        borderColor: Int = Color.WHITE,
        borderWidth: Float = 2f,
        padding: Padding = Padding(20f),
        cellPadding: Padding = Padding(10f),
        position: Position = LeftBottom,
        shape: RoundedCornerShape = RoundedCornerShape(20f),
        rotation: Float = 0f // 旋转角度
    ): Bitmap {
        // 创建图像副本
        val mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
        // 添加画布
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
            this.color = headerTextColor
            this.textSize = headerTextSize
            this.typeface = Typeface.DEFAULT_BOLD
        }

        // 计算表格尺寸
        val rows = tableData.size
        val cols = tableData[0].size
        // 单元宽度
        val cellWidths = FloatArray(cols)
        // 行高
        val rowHeights = FloatArray(rows)

        // 矩形
        val textBounds = Rect()

        // 表头宽度和高度
        var headerWidth = 0f
        var headerHeight = 0f

        // 判断标题是否为空
        if (headerTitle != null) {
            // 获取字符串的范围
            headerPaint.getTextBounds(headerTitle, 0, headerTitle.length, textBounds)
            // 计算表头的宽高
            headerWidth = headerPaint.measureText(headerTitle) + headerPadding.horizontal
            headerHeight = textBounds.height() + headerPadding.vertical * 2
        }

        // 预计算单元格尺寸
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                // 当前字符内容
                val cellContent = tableData[i][j]
                // 判断当前内容是否过长
                if (cellContent.length <= maxLineLength) {
                    textPaint.getTextBounds(cellContent, 0, cellContent.length, textBounds)
                    cellWidths[j] =
                        max(
                            cellWidths[j],
                            textPaint.measureText(cellContent) + cellPadding.horizontal * 2
                        )
                    rowHeights[i] =
                        max(rowHeights[i], textBounds.height().toFloat() + cellPadding.vertical * 2)
                } else {
                    // 计算字符串可以分多少行
                    val lines = ceil(cellContent.length / maxLineLength.toFloat()).toInt()
                    var lineMaxWidth = 0f
                    var lineMaxHeight = 0f
                    for (k in 0 until lines) {

                        val line =
                            if ((k + 1) * maxLineLength < cellContent.length) cellContent.substring(
                                k * maxLineLength,
                                (k + 1) * maxLineLength
                            )
                            else cellContent.substring(
                                k * maxLineLength,
                                cellContent.length
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
            calculatingTablePosition(originalBitmap, padding, position, tableWidth, tableHeight)
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

                val headerShape = shape.copy(bottomStart = 0f, bottomEnd = 0f)
                // 画圆角头
                headerShape.draw(canvas, headerColor, headerRect, borderWidth = borderWidth)
                // 画圆角头边界
                headerShape.draw(
                    canvas,
                    borderColor,
                    headerRect,
                    borderWidth = borderWidth,
                    style = Paint.Style.STROKE
                )
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

                    if (i == rows - 1 && j == 0) {
                        // 左下角圆角
                        val leftShape = shape.copy(topStart = 0f, topEnd = 0f, bottomEnd = 0f)
                        leftShape.draw(canvas, cellColor, cellRect)
                        leftShape.draw(
                            canvas,
                            borderColor,
                            cellRect,
                            borderWidth = borderWidth,
                            style = Paint.Style.STROKE
                        )
                    } else if ((i == rows - 1 && j == cols - 1)) {
                        // 右下角圆角
                        val rightShape = shape.copy(topStart = 0f, topEnd = 0f, bottomStart = 0f)
                        rightShape.draw(canvas, cellColor, cellRect)
                        rightShape.draw(
                            canvas,
                            borderColor,
                            cellRect,
                            borderWidth = borderWidth,
                            style = Paint.Style.STROKE
                        )
                    } else {
                        drawRect(cellRect, cellPaint)
                        drawRect(cellRect, borderPaint)
                    }

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
                        // 判断是否为第一列
                        if (j == 0) {
                            textPaint.typeface = Typeface.DEFAULT_BOLD
                        } else {
                            textPaint.typeface = Typeface.DEFAULT
                        }
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

    private fun calculatingTablePosition(
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