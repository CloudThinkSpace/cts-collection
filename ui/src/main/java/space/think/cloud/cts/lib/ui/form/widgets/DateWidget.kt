package space.think.cloud.cts.lib.ui.form.widgets

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import java.text.SimpleDateFormat
import java.util.*

/**
 * ClassName: DateWidget
 * Description:
 * @date: 2022/10/16 14:45
 * @author: tanghy
 */
@SuppressLint("SimpleDateFormat")
@Composable
fun DateWidget(
    modifier: Modifier = Modifier,
    value: String,
    title: String,
    format: String? = null,
    unit: String? = null,
    required: Boolean = false,
    errorMsg: String? = null,
    isError: Boolean = false,
    description: String? = null,
    enabled: Boolean = true,
    textStyle: TextStyle = TextStyle.Default,
    cursorBrush: Brush = SolidColor(Color.Black),
    onValueChange: (String) -> Unit,
) {

    val context = LocalContext.current

    val datePickerDialog by lazy {
        val simpleDateFormat = SimpleDateFormat(format ?: "yyyy年MM月dd日")
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(context, { _: DatePicker, y: Int, m: Int, dayOfMonth: Int ->
            calendar.set(y, m, dayOfMonth)
            val selectDate = calendar.time
            val currentDate = simpleDateFormat.format(selectDate)
            onValueChange(currentDate)
        }, year, month, day)
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
        readOnly = true,
        textStyle,
        keyboardOptions = KeyboardOptions.Default,
        keyboardActions = KeyboardActions.Default,
        singleLine = false,
        maxLines = Int.MAX_VALUE,
        visualTransformation = VisualTransformation.None,
        interactionSource = remember { MutableInteractionSource() }
            .also { interactionSource ->
                LaunchedEffect(interactionSource) {
                    interactionSource.interactions.collect {
                        if (it is PressInteraction.Release) {
                            datePickerDialog.show()
                        }
                    }
                }
            },
        cursorBrush,
        trailingIcon = {
            Icon(
                Icons.Filled.DateRange,
                contentDescription = "",
                modifier = Modifier.clickable {
                    if (enabled) {
                        datePickerDialog.show()
                    }
                },
                tint = Color.Gray
            )
        }
    )

}