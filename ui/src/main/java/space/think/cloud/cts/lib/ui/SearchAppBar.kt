package space.think.cloud.cts.lib.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchAppBar(
    modifier: Modifier,
    searchValue: String,
    placeholder: String = "请输入内容",
    onClear: (() -> Unit)? = null,
    updateValue: (String) -> Unit
) {

    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .border(1.dp, Color.White, RoundedCornerShape(3.dp))
        ) {
            Image(
                Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier
                    .padding(5.dp)
                    .align(Alignment.CenterVertically),
                colorFilter = ColorFilter.tint(Color.White)
            )
            BasicTextField(
                value = searchValue,
                onValueChange = updateValue,
                textStyle = MaterialTheme.typography.labelMedium.copy(
                    color = Color.White
                ),
                maxLines = 1,
                cursorBrush = SolidColor(LocalContentColor.current),
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
                    .padding(5.dp),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        // 如果文本为空，显示提示
                        if (searchValue.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = Color.Gray,
                                style = TextStyle(
                                    fontSize = 16.sp,
                                    // 可以设置其他样式，如字体等
                                )
                            )
                        }
                        innerTextField() // 这是实际的输入框，必须调用
                    }
                }
            )
            if (searchValue.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(5.dp)
                        .clickable {
                            onClear?.invoke()
                        }
                        .align(Alignment.CenterVertically),
                ) {
                    Icon(imageVector = Icons.Default.Clear, null, tint = Color.White)
                }

            }

        }
    }


}