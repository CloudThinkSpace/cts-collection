package space.think.cloud.cts.lib.ui.form.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

/**
 * ClassName: TextWidget
 * Description:
 * @date: 2022/10/14 11:12
 * @author: tanghy
 */
@Composable
fun BaseWidget(
    modifier: Modifier = Modifier,
    value: String,
    title: String,
    unit: String? = null,
    required: Boolean = false,
    errorMsg: String? = null,
    isError: Boolean = false,
    description: String? = null,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    cursorBrush: Brush = SolidColor(Color.Black),
    trailingIcon: @Composable (() -> Unit)? = null,
) {


    Widget(
        modifier = modifier,
        title = title,
        required = required,
        errorMsg = errorMsg,
        isError = isError,
        description = description,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
                .border(
                    .5.dp,
                    if (isError) Color.Red else Color.Gray,
                    shape = RoundedCornerShape(5.dp)
                )
                .background(
                    if (enabled) Color.White else Color(0xFFE9E9E9),
                    RoundedCornerShape(5.dp)
                )
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = value,
                textStyle = textStyle.copy(fontSize = MaterialTheme.typography.titleMedium.fontSize),
                readOnly = readOnly,
                enabled = enabled,
                maxLines = maxLines,
                visualTransformation = visualTransformation,
                singleLine = singleLine,
                keyboardOptions = keyboardOptions,
                keyboardActions = keyboardActions,
                cursorBrush = cursorBrush,
                interactionSource = interactionSource,
                modifier = modifier.weight(1f),
                onValueChange = {
                    onValueChange(it)
                },
            )

            if (unit?.isNotBlank() == true) {
                Text(
                    text = unit,
                    fontSize = MaterialTheme.typography.titleSmall.fontSize,
                    color = MaterialTheme.typography.labelSmall.color
                )
            }
            trailingIcon?.invoke()

        }

    }


}