package space.think.cloud.cts.lib.ui.form.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import space.think.cloud.cts.lib.ui.form.Item

/**
 * ClassName: RadioWidget
 * Description:
 * @date: 2023/1/12 19:56
 * @author: tanghy
 */
@Composable
fun RadioWidget(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    items: List<Item>,
    required: Boolean = false,
    errorMsg: String? = null,
    isError: Boolean = false,
    description: String? = null,
    enabled: Boolean = true,
    textStyle: TextStyle = TextStyle.Default,
    onValueChange: (Item) -> Unit,
) {

    Widget(
        modifier = modifier,
        title = title,
        required = required,
        errorMsg = errorMsg,
        isError = isError,
        description = description,
    ) {
        Column(
            modifier = Modifier
                .border(
                    .5.dp, if (isError) Color.Red else Color.Gray, shape = RoundedCornerShape(5.dp)
                )
                .background(
                    if (enabled) Color.White else Color(0xFFE9E9E9), RoundedCornerShape(5.dp)
                )
                .fillMaxWidth()
        ) {
            items.forEachIndexed { index, item ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = value == item.code, onClick = {
                            // 去除已选中的item
                            items.forEach {
                                it.isCheck = false
                            }
                            item.isCheck = true
                            onValueChange(item)
                        }, enabled = enabled
                    )
                    Text(
                        modifier = Modifier.clickable {
                            if (enabled) {
                                // 去除已选中的item
                                items.forEach {
                                    it.isCheck = false
                                }
                                item.isCheck = true
                                onValueChange(item)
                            }
                        },
                        text = item.name, style = textStyle
                    )
                }
                if (index < items.lastIndex) {
                    HorizontalDivider(
                        color = Color.LightGray,
                        thickness = 0.3.dp,
                        modifier = Modifier.padding(horizontal = 0.dp)
                    )
                }
            }

        }
    }
}