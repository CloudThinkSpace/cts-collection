package space.think.cloud.cts.lib.ui.form.widgets

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import space.think.cloud.cts.lib.ui.form.ImageItem
import space.think.cloud.cts.lib.ui.form.VideoView
import space.think.cloud.cts.lib.ui.utils.StringUtil
import kotlin.math.ceil

/**
 * ClassName: ImageWidget
 * Description:
 * @date: 2022/10/16 15:48
 * @author: tanghy
 * @param lineMaxNum 一行最多有几张图片
 */
@Composable
fun VideoWidget(
    modifier: Modifier = Modifier,
    value: String,
    size: Dp = 80.dp,
    title: String,
    subTitles: List<String>,
    lineMaxNum: Int = 4,
    required: Boolean = false,
    errorMsg: String? = null,
    isError: Boolean = false,
    description: String? = null,
    enabled: Boolean = true,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    onPreview: ((Uri) -> Unit)? = null,
    onChangeValue: (String) -> Unit,
) {

    val localSoftwareKeyboardController = LocalSoftwareKeyboardController.current
    // 解析数据
    val newValue = StringUtil.jsonToMap(value)
    // 是否显示删除图片Dalog
    var isOpenDelete by remember {
        mutableStateOf(false)
    }
    // 图片当前位置
    var currentImageIndex by remember {
        mutableIntStateOf(0)
    }
    // 选择的位置
    var selectIndex by remember {
        mutableIntStateOf(0)
    }

    Widget(
        modifier = modifier,
        title = title,
        required = required,
        errorMsg = errorMsg,
        isError = isError,
        description = description,
    ) {
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
                    VideoView(
                        index = index,
                        size = size,
                        title = subTitles[index],
                        uri = newValue[index]?.path,
                        isError = isError,
                        loading = newValue[index]?.loading == true,
                        onClick = {
                            selectIndex = index
                        },
                        onDelete = {
                            if (enabled) {
                                isOpenDelete = true
                                currentImageIndex = index
                            }
                        },
                        onPreview = onPreview
                    ) {  path ->
                        localSoftwareKeyboardController?.hide()
                        if (enabled) {
                            val temp = newValue.toMutableMap()
                            temp[selectIndex] = ImageItem(name = subTitles[selectIndex], path = path)
                            val json = StringUtil.mapToString(temp)
                            onChangeValue(json)
                        }

                    }
                }
            }

        }
    }

    if (isOpenDelete) AlertDialog(
        onDismissRequest = {
            isOpenDelete = false
        },
        shape = RoundedCornerShape(5.dp),
        title = { Text(text = "操作提示") },
        text = { Text(text = "确认要删除视频？", color = Color.Gray) },
        confirmButton = {
            TextButton(onClick = {
                isOpenDelete = false
                val temp = newValue.toMutableMap()
                temp.remove(currentImageIndex)
                val json = StringUtil.mapToString(temp)
                onChangeValue(json)

            }) {
                Text(text = "确定")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                isOpenDelete = false
            }) {
                Text(text = "取消")
            }
        })


}