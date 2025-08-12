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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import space.think.cloud.cts.lib.ui.form.ImageView
import space.think.cloud.cts.lib.ui.form.MediaItem
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
    value: Map<Int, MediaItem>,
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
    onDelete: (Int) -> Unit,
    onChangeValue: suspend (String, index: Int) -> Unit,
) {

    // 键盘控制器
    val localSoftwareKeyboardController = LocalSoftwareKeyboardController.current
    // 是否显示删除图片Dialog
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

    val scope = rememberCoroutineScope()

    // 加载中
    var loading by remember {
        mutableStateOf(false)
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
                    ImageView(
                        size = size,
                        title = subTitles[index],
                        uri = value[index]?.path?.toUri(),
                        isError = isError,
                        loading = selectIndex == index && loading,
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
                    ) { path ->
                        localSoftwareKeyboardController?.hide()
                        loading = true
                        path?.let {
                            // 处理图片，添加水印
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    onChangeValue(it, selectIndex)
                                }
                                loading = false
                            }
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
        text = { Text(text = "确认要删除照片？", color = Color.Gray) },
        confirmButton = {
            TextButton(onClick = {
                isOpenDelete = false
                onDelete(currentImageIndex)

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