package space.think.cloud.cts.lib.ui.form.widgets

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import space.think.cloud.cts.lib.ui.form.ImageItem
import space.think.cloud.cts.lib.ui.form.ImageView
import kotlin.math.ceil

/**
 * ClassName: ImageWidget
 * Description:
 * @date: 2022/10/16 15:48
 * @author: tanghy
 * @param lineMaxNum 一行最多有几张图片
 */
@Composable
fun ImageWidget(
    modifier: Modifier = Modifier,
    value: Map<Int, ImageItem>,
    size: Dp = 80.dp,
    title: String,
    subTitles: List<String> = listOf(""),
    lineMaxNum: Int = 4,
    required: Boolean = false,
    errorMsg: String? = null,
    isError: Boolean = false,
    description: String? = null,
    enabled: Boolean = true,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    onPreview: ((Uri) -> Unit)? = null,
    onDelete: ((Int) -> Unit)? = null,
    onClick: (Int, String) -> Unit,
) {

    val localSoftwareKeyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 10.dp, end = 10.dp, bottom = 5.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Row {
            // 是否显示必填符号
            if (required) {
                Text(
                    text = "*",
                    fontWeight = FontWeight.Bold,
                    fontSize = MaterialTheme.typography.titleMedium.fontSize,
                    color = Color.Red
                )
            }
            // 标题
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = MaterialTheme.typography.titleMedium.fontSize,
                color = MaterialTheme.typography.titleMedium.color
            )
        }
        // 说明文字
        if (description?.isNotBlank() == true) {
            Text(
                text = description,
                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                fontFamily = FontFamily.Monospace,
                color = Color.Gray
            )
        }
        // 计算总行数
        val rows = ceil(subTitles.size.toDouble() / lineMaxNum).toInt()

        // 遍历行数
        for (i in 0 until rows) {

            // 计算每行的个数
            val rowNum = if (subTitles.size > lineMaxNum * (i + 1)) lineMaxNum else {
                subTitles.size - lineMaxNum * i
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = horizontalArrangement,
            ) {
                repeat(rowNum) {
                    // 当前图片位置
                    val index = it + i * lineMaxNum
                    // 当前位置的图片是否存在
                    ImageView(
                        size = size,
                        title = subTitles[index],
                        uri = value[index]?.path,
                        isError = isError,
                        loading = value[index]?.loading ?: false,
                        onDelete = {
                            onDelete?.invoke(index)
                        },
                        onPreview = onPreview
                    ) {
                        localSoftwareKeyboardController?.hide()
                        if (enabled) {
                            // 点击的图片位置
                            onClick(
                                index,
                                subTitles[index]
                            )
                        }

                    }
                }
            }

        }

        // 显示错误信息
        if (isError) {
            Text(
                text = errorMsg ?: "",
                fontSize = MaterialTheme.typography.labelSmall.fontSize,
                color = Color.Red,
                fontStyle = FontStyle.Italic,
                fontFamily = FontFamily.Monospace
            )
        }
    }

}