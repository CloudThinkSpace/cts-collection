package space.think.cloud.cts.lib.ui.form.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun Widget(
    modifier: Modifier = Modifier,
    title: String,
    required: Boolean = false,
    errorMsg: String? = null,
    isError: Boolean = false,
    description: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
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
        content()
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