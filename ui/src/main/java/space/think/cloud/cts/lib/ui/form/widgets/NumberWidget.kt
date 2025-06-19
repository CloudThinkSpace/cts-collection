package space.think.cloud.cts.lib.ui.form.widgets

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import space.think.cloud.cts.lib.ui.utils.KeyboardTypeUtil

/**
 * ClassName: NumberWidget
 * Description:
 * @date: 2022/10/15 19:32
 * @author: tanghy
 */
@Composable
fun NumberWidget(
    modifier: Modifier = Modifier,
    value: String,
    title: String,
    unit: String? = null,
    required: Boolean = false,
    errorMsg: String? = null,
    isError: Boolean = false,
    description: String? = null,
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
    onValueChange: (String) -> Unit,
) {

    BaseWidget(
        modifier,
        value,
        title,
        unit,
        required,
        errorMsg,
        isError,
        description,
        onValueChange = {
            val keepDigital = KeyboardTypeUtil.keepDigital(it,"[0-9\\.\\-]")
            onValueChange(keepDigital)
        },
        enabled,
        readOnly,
        textStyle,
        keyboardOptions.copy(
            keyboardType = KeyboardType.Decimal
        ),
        keyboardActions,
        singleLine,
        maxLines,
        visualTransformation,
        interactionSource,
        cursorBrush
    )

}