package space.think.cloud.cts.lib.watermask

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.core.graphics.withRotation
import space.think.cloud.cts.lib.watermask.utils.CanvasUtil
import kotlin.math.ceil
import kotlin.math.max


/***
 * @param tableData 数据数组
 * @param textColor 字体颜色
 * @param textVerticalCenter 是否居中
 * @param maxLineLength 行最大字符串
 * @param lineSpace 行间距
 * @param headerTitle 标题
 * @param headerColor 标题背景色
 * @param headerTextColor 标题文字颜色
 * @param headerPadding 标题间距
 * @param cellPadding 单元格间距
 * @param borderWidth 边框宽度
 * @param borderColor 边框颜色
 * @param margin 偏移
 * @param cellColor 单元格颜色
 * @param position 水印的位置
 * @param shape 圆角
 * @param minWidth 最小宽度
 * @param rotation 旋转
 */

class Watermark(
    private val tableData: List<List<String>>,
    private val textColor: Int = Color.argb(255, 20, 20, 20),
    private val textSize: Float = 60f,
    private val textVerticalCenter: Boolean = false,
    private val maxLineLength: Int = 20, // 每行最大字符数
    private val lineSpace: Float = 0f,
    private val headerTitle: String? = null,
    private val headerColor: Int = Color.argb(255, 50, 120, 255),
    private val headerTextSize: Float = 86f,
    private val headerTextColor: Int = Color.WHITE,
    private val headerPadding: Padding = Padding(10f, 20f),
    private val cellColor: Int = Color.argb(150, 255, 255, 255),
    private val borderColor: Int = Color.WHITE,
    private val borderWidth: Float = 0f,
    private val cellBorderWidth: Float = 0f,
    private val cellBorderColor: Int = Color.WHITE,
    private val margin: Margin = Margin(20f),
    private val cellPadding: Padding = Padding(10f),
    private val position: Position = LeftBottom,
    private val shape: RoundedCornerShape = RoundedCornerShape(20f),
    private val rotation: Float = 0f, // 旋转角度
    private val minWidth: Float = 1000f,
) : IWatermark {

    // 设置header文字画笔
    private val headerPaint by lazy {
        Paint().apply {
            this.color = headerTextColor
            this.textSize = headerTextSize
            this.typeface = Typeface.DEFAULT_BOLD
        }
    }

    private val textPaint by lazy {
        val textSize = this.textSize
        Paint().apply {
            this.color = textColor
            this.textSize = textSize
            this.isAntiAlias = true
            this.textAlign = Paint.Align.LEFT
        }
    }

    // 计算表格尺寸
    private val rows = tableData.size
    private val cols = tableData[0].size

    // 表头宽度和高度
    private var headerWidth = 0f
    private var headerHeight = 0f

    // 单元格画笔
    private val cellPaint by lazy {
        Paint().apply { isAntiAlias = true }
    }

    // 边框画笔
    private val borderPaint by lazy {
        Paint().apply {
            color = cellBorderColor
            style = Paint.Style.STROKE
            strokeWidth = cellBorderWidth
            isAntiAlias = true
        }
    }

    init {
        headerTitle?.let {
            val fontMetrics: Paint.FontMetrics = headerPaint.fontMetrics
            val textHeight = fontMetrics.descent - fontMetrics.ascent
            // 计算表头的宽高
            headerWidth =
                headerPaint.measureText(headerTitle) + headerPadding.left + headerPadding.right
            headerHeight = textHeight + headerPadding.top + headerPadding.bottom
        }
    }


    override fun draw(
        originalBitmap: Bitmap,
    ): Bitmap {
        // 创建图像副本
        val mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
        // 添加画布
        val canvas = Canvas(mutableBitmap)
        // 计算表格每个单元的范围
        val table = CanvasUtil.calculateTableCellsExtent(
            textPaint, tableData, cellPadding, maxLineLength, lineSpace
        )
        val rowHeights = table.rows
        val cellWidths = table.columns

        // 表格的宽和高
        val tableWidth = max(max(cellWidths.sum(), minWidth), headerWidth)
        val tableHeight = rowHeights.sum() + headerHeight

        // 计算表格位置
        val currentPosition =
            calculatingTablePosition(originalBitmap, tableWidth, tableHeight)
        // 表格起始坐标
        val tableLeft = currentPosition.first
        val tableTop = currentPosition.second

        // 应用旋转
        canvas.withRotation(
            rotation,
            tableLeft,
            tableTop
        ) {

            // 返回绘制后的位置顶点
            var currentTop = drawHeader(canvas, tableLeft, tableTop, tableWidth)

            for (i in 0 until rows) {
                var currentLeft = tableLeft

                for (j in 0 until cols) {
                    // 设置单元格样式
                    cellPaint.color = cellColor

                    // 绘制单元格
                    // 判断是否为每行的最后一个单元格重新计算单元格大小
                    val cellRect = if (j == cols - 1) {
                        val lastCellWidth = tableWidth - (cellWidths.sum() - cellWidths[j])
                        RectF(
                            currentLeft,
                            currentTop,
                            currentLeft + lastCellWidth,
                            currentTop + rowHeights[i]
                        )
                    } else {
                        RectF(
                            currentLeft,
                            currentTop,
                            currentLeft + cellWidths[j],
                            currentTop + rowHeights[i]
                        )
                    }

                    if (i == rows - 1 && j == 0) {
                        // 左下角圆角
                        val leftShape = shape.copy(topStart = 0f, topEnd = 0f, bottomEnd = 0f)
                        leftShape.draw(canvas, cellColor, cellRect)
                        leftShape.draw(
                            canvas,
                            cellBorderColor,
                            cellRect,
                            borderWidth = cellBorderWidth,
                            style = Paint.Style.STROKE
                        )
                    } else if ((i == rows - 1 && j == cols - 1)) {
                        // 右下角圆角
                        val rightShape = shape.copy(topStart = 0f, topEnd = 0f, bottomStart = 0f)
                        rightShape.draw(canvas, cellColor, cellRect)
                        rightShape.draw(
                            canvas,
                            cellBorderColor,
                            cellRect,
                            borderWidth = cellBorderWidth,
                            style = Paint.Style.STROKE
                        )
                    } else {
                        drawRect(cellRect, cellPaint)
                        drawRect(cellRect, borderPaint)
                    }

                    // 绘制文字
                    val textX = currentLeft + cellPadding.left

                    // 当前字符内容
                    val currentContent = tableData[i][j]
                    // 计算字符串可以分多少行
                    val lines = ceil(currentContent.length / maxLineLength.toFloat()).toInt()
                    // 字符串的高度
                    val fontHeight = textPaint.ascent() + textPaint.descent()
                    //  如果是单行，文字写单元格起始位置上
                    if (lines == 1 && !textVerticalCenter) {
                        val textY = currentTop + cellPadding.top - textPaint.ascent()
                        // 判断是否为第一列
                        if (j == 0) {
                            textPaint.typeface = Typeface.DEFAULT_BOLD
                        } else {
                            textPaint.typeface = Typeface.DEFAULT
                        }
                        // 绘制文字
                        drawText(currentContent, textX, textY, textPaint)
                    } else {
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
                                (rowHeights[i] - cellPadding.top - cellPadding.bottom - (lines - 1) * lineSpace) / lines

                            // 字符串的y位置
                            val textY =
                                currentTop +
                                        rowHeight * k +
                                        rowHeight * 1 / 2 -
                                        fontHeight / 2 + cellPadding.top + k * lineSpace
                            // 判断是否为第一列
                            if (j == 0) {
                                textPaint.typeface = Typeface.DEFAULT_BOLD
                            } else {
                                textPaint.typeface = Typeface.DEFAULT
                            }
                            // 绘制文字
                            drawText(line, textX, textY, textPaint)
                        }
                    }

                    currentLeft += cellWidths[j]
                }

                currentTop += rowHeights[i]
            }

            // 绘制外边框
            val tableRect = RectF(
                tableLeft,
                tableTop,
                tableLeft + tableWidth,
                tableTop + tableHeight
            )
            shape.draw(
                canvas,
                borderColor,
                tableRect,
                borderWidth = borderWidth,
                style = Paint.Style.STROKE
            )

        }
        return mutableBitmap
    }

    private fun drawHeader(
        canvas: Canvas,
        tableLeft: Float,
        tableTop: Float,
        tableWidth: Float,

        ): Float {
        // 判断是否有表头
        if (headerTitle != null) {
            // 表头颜色
            cellPaint.color = headerColor
            // 绘制单元格
            val headerRect = RectF(
                tableLeft,
                tableTop,
                tableLeft + tableWidth,
                tableTop + headerHeight
            )
            // 圆角退休
            val headerShape = shape.copy(bottomStart = 0f, bottomEnd = 0f)
            // 画圆角头
            headerShape.draw(canvas, headerColor, headerRect, borderWidth = cellBorderWidth)
            // 画圆角头边界
            headerShape.draw(
                canvas,
                cellBorderColor,
                headerRect,
                borderWidth = cellBorderWidth,
                style = Paint.Style.STROKE
            )
            // 表头位置
            val headerX = tableLeft + tableWidth / 2 - headerWidth / 2
            val headerY =
                tableTop - headerPaint.ascent() + headerPadding.top
            // 绘制文字
            canvas.drawText(
                headerTitle,
                headerX,
                headerY,
                headerPaint
            )

            return tableTop + headerHeight
        } else {
            return tableTop
        }

    }

    private fun calculatingTablePosition(
        originalBitmap: Bitmap,
        tableWidth: Float,
        tableHeight: Float,
    ): Pair<Float, Float> {

        // 计算表格位置
        var tableLeft = 0f
        var tableTop = 0f
        when (position) {
            is LeftTop -> {
                tableLeft = margin.left
                tableTop = margin.top
            }

            is RightTop -> {
                tableLeft = originalBitmap.width - tableWidth - margin.right
                tableTop = margin.top
            }

            is LeftBottom -> {
                tableLeft = margin.left
                tableTop = originalBitmap.height - tableHeight - margin.bottom
            }

            is RightBottom -> {
                tableLeft = originalBitmap.width - tableWidth - margin.right
                tableTop = originalBitmap.height - tableHeight - margin.bottom
            }

            is TopCenter -> {
                tableLeft = (originalBitmap.width - tableWidth) / 2
                tableTop = margin.top
            }

            is BottomCenter -> {
                tableLeft = (originalBitmap.width - tableWidth) / 2
                tableTop = originalBitmap.height - tableHeight - margin.bottom
            }

            is LeftCenter -> {
                tableLeft = margin.left
                tableTop = (originalBitmap.height - tableHeight) / 2
            }

            is RightCenter -> {
                tableLeft = originalBitmap.width - tableWidth - margin.right
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