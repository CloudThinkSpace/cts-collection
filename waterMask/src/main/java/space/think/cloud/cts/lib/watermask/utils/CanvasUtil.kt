package space.think.cloud.cts.lib.watermask.utils

import android.graphics.Paint
import android.graphics.Typeface
import space.think.cloud.cts.lib.watermask.Padding
import space.think.cloud.cts.lib.watermask.Rectangle
import space.think.cloud.cts.lib.watermask.Table
import kotlin.math.max

object CanvasUtil {
    /**
     * 计算字文字的范围
     * @param textPaint 画笔
     * @param content 文字内容
     * @param maxLength 每行最大长度
     * @param padding 文字的边距
     * @param lineSpace 如果字符串被分成多行，每行的行间距
     */
    fun calculateTextExtent(
        textPaint: Paint,
        content: String,
        maxLength: Int = Int.MAX_VALUE,
        padding: Padding = Padding(0f),
        lineSpace: Float = 0f
    ): Rectangle {
        // 计算行数
        val lines = StringUtil.lines(content, maxLength)
        // 矩形范围
        val rectangle = Rectangle(0f, 0f)
        // 字体测量对象
        val fontMetrics: Paint.FontMetrics = textPaint.fontMetrics
        // 行高
        val textHeight = fontMetrics.descent - fontMetrics.ascent
        // 遍历多行文字
        for (k in 0 until lines) {
            // 计算字符串的总长度
            val contentLength = content.length
            // 每行字符串
            val lineStr =
                if ((k + 1) * maxLength < contentLength)
                    content.substring(
                        k * maxLength,
                        (k + 1) * maxLength
                    )
                else content.substring(
                    k * maxLength,
                    contentLength
                )

            // 行宽
            rectangle.width = max(
                rectangle.width,
                textPaint.measureText(lineStr) + padding.left + padding.right
            )
            rectangle.height += textHeight + lineSpace + padding.top + padding.bottom
        }
        // 减去多出的一个行高间隔
        rectangle.height -= lineSpace
        return rectangle
    }

    /**
     * 计算表格的范围
     * @param textPaint 画笔
     * @param tableData 字符二维数组
     * @param padding 边距
     * @param maxLength 每行最大字符串长度
     * @param lineSpace 被折叠的行间距
     */
    fun calculateTableExtent(
        textPaint: Paint,
        tableData: List<List<String>>,
        padding: Padding = Padding(0f),
        maxLength: Int = Int.MAX_VALUE,
        lineSpace: Float = 0f
    ): Rectangle {

        val table = calculateTableCellsExtent(
            textPaint = textPaint,
            tableData = tableData,
            padding = padding,
            maxLength = maxLength,
            lineSpace = lineSpace
        )

        return Rectangle(
            width = table.rows.sum(),
            height = table.columns.sum()
        )
    }

    /**
     * 计算表格每个单元格范围,返回表格数组
     * @param textPaint 画笔
     * @param tableData 字符二维数组
     * @param padding 边距
     * @param maxLength 每行最大字符串长度
     * @param lineSpace 被折叠的行间距
     */
    fun calculateTableCellsExtent(
        textPaint: Paint,
        tableData: List<List<String>>,
        padding: Padding = Padding(0f),
        maxLength: Int = Int.MAX_VALUE,
        lineSpace: Float = 0f
    ): Table {
        // 计算表格尺寸
        val rows = tableData.size
        val cols = tableData[0].size
        // 单元宽度
        val cellWidths = FloatArray(cols)
        // 边框高度
        val rowHeights = FloatArray(rows)
        // 预计算单元格尺寸
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                // 判断是否为第一列,第一列标题列，加粗
                if (j == 0) {
                    textPaint.typeface = Typeface.DEFAULT_BOLD
                } else {
                    textPaint.typeface = Typeface.DEFAULT
                }
                // 当前字符内容
                val cellContent = tableData[i][j]
                val fontMetrics: Paint.FontMetrics = textPaint.fontMetrics
                // 行高
                val textHeight = fontMetrics.descent - fontMetrics.ascent
                // 判断当前内容是否过长
                if (cellContent.length <= maxLength) {
                    // 单元格宽度
                    cellWidths[j] =
                        max(
                            cellWidths[j],
                            textPaint.measureText(cellContent) + padding.left + padding.right
                        )
                    // 字体高度
                    val fontHeight = textHeight
                    // 不同单元格高度
                    rowHeights[i] =
                        max(rowHeights[i], fontHeight + padding.top + padding.bottom)
                } else {
                    // 计算字符串分割后所站的范围
                    val rectangle =
                        calculateTextExtent(textPaint, cellContent, maxLength, padding, lineSpace)
                    cellWidths[j] = rectangle.width
                    rowHeights[i] = rectangle.height + padding.top + padding.bottom
                }
            }
        }

        return Table(
            rows = rowHeights,
            columns = cellWidths
        )
    }
}