package space.think.cloud.cts.lib.watermark.impl

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import androidx.core.graphics.createBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import space.think.cloud.cts.lib.watermark.IWatermark
import space.think.cloud.cts.lib.watermark.Padding
import space.think.cloud.cts.lib.watermark.RoundedCornerShape
import space.think.cloud.cts.lib.watermark.utils.CanvasUtil
import space.think.cloud.cts.lib.watermark.utils.StringUtil
import kotlin.math.max

class TableWatermark(
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
    private val cellColor: Int = Color.argb(200, 255, 255, 255),
    private val borderColor: Int = Color.argb(255, 70, 70, 70),
    private val borderWidth: Float = 0f,
    private val cellBorderWidth: Float = 0f,
    private val cellBorderColor: Int = Color.WHITE,
    private val cellPadding: Padding = Padding(10f),
    private val shape: RoundedCornerShape = RoundedCornerShape(20f),
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

    override suspend fun draw(): Bitmap {
        // 启用协程处理添加水印
        return withContext(Dispatchers.IO) {

            // 计算表格每个单元的范围
            val table = CanvasUtil.calculateTableCellsExtent(
                textPaint, tableData, cellPadding, maxLineLength, lineSpace
            )
            val rowHeights = table.rows
            val cellWidths = table.columns

            // 表格的宽和高
            val tableWidth = max(max(cellWidths.sum(), minWidth), headerWidth)
            val tableHeight = rowHeights.sum() + headerHeight
            // 创建水印
            val bitmap = createBitmap(tableWidth.toInt(), tableHeight.toInt())
            // 创建画布
            val canvas = Canvas(bitmap)
            // 返回绘制后的位置顶点
            var currentTop = drawHeader(canvas, tableWidth)
            for (i in 0 until rows) {
                var currentLeft = 0.0f

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
                        val rightShape =
                            shape.copy(topStart = 0f, topEnd = 0f, bottomStart = 0f)
                        rightShape.draw(canvas, cellColor, cellRect)
                        rightShape.draw(
                            canvas,
                            cellBorderColor,
                            cellRect,
                            borderWidth = cellBorderWidth,
                            style = Paint.Style.STROKE
                        )
                    } else {
                        canvas.drawRect(cellRect, cellPaint)
                        canvas.drawRect(cellRect, borderPaint)
                    }

                    // 绘制文字
                    val textX = currentLeft + cellPadding.left

                    // 当前字符内容
                    val currentContent = tableData[i][j]
                    // 计算字符串可以分多少行
                    val lines = StringUtil.lines(currentContent, maxLineLength).size
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
                        canvas.drawText(currentContent, textX, textY, textPaint)
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
                            canvas.drawText(line, textX, textY, textPaint)
                        }
                    }

                    currentLeft += cellWidths[j]
                }

                currentTop += rowHeights[i]
            }

            // 绘制外边框
            val tableRect = RectF(
                0f,
                0f,
                tableWidth,
                tableHeight
            )
            shape.draw(
                canvas,
                borderColor,
                tableRect,
                borderWidth = borderWidth,
                style = Paint.Style.STROKE
            )
            bitmap
        }
    }

    private fun drawHeader(
        canvas: Canvas,
        tableWidth: Float,

        ): Float {
        // 判断是否有表头
        if (headerTitle != null) {
            // 表头颜色
            cellPaint.color = headerColor
            // 绘制单元格
            val headerRect = RectF(
                0f,
                0f,
                tableWidth,
                headerHeight
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
            val headerX = tableWidth / 2 - headerWidth / 2
            val headerY = -headerPaint.ascent() + headerPadding.top
            // 绘制文字
            canvas.drawText(
                headerTitle,
                headerX,
                headerY,
                headerPaint
            )

            return headerHeight
        } else {
            return 0f
        }

    }
}