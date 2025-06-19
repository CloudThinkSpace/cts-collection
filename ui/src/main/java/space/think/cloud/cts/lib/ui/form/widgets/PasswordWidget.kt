package space.think.cloud.cts.lib.ui.form.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

/**
 * ClassName: PasswordWidget
 * Description:
 * @date: 2022/10/15 20:12
 * @author: tanghy
 */
@Composable
fun PasswordWidget(
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
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    cursorBrush: Brush = SolidColor(Color.Black),
    onValueChange: (String) -> Unit,
) {

    var isPassword by remember {
        mutableStateOf(true)
    }

    BaseWidget(
        modifier,
        value,
        title,
        unit,
        required,
        errorMsg,
        isError,
        description,
        onValueChange,
        enabled,
        readOnly,
        textStyle,
        keyboardOptions.copy(
            keyboardType = KeyboardType.Password
        ),
        keyboardActions,
        singleLine,
        maxLines,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        interactionSource,
        cursorBrush,
        trailingIcon = {
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                    isPassword = !isPassword
                }
                ,
                imageVector = if (isPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                contentDescription = "",
                tint = Color.Gray
            )
        }
    )

}