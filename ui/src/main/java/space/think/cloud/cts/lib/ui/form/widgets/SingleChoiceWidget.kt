package space.think.cloud.cts.lib.ui.form.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import space.think.cloud.cts.lib.ui.form.Item

/**
 * ClassName: SingleChoiceWidget
 * Description:
 * @date: 2022/10/15 21:26
 * @author: tanghy
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleChoiceWidget(
    modifier: Modifier = Modifier,
    value: String,
    title: String,
    items: List<Item>,
    unit: String? = null,
    required: Boolean = false,
    errorMsg: String? = null,
    isError: Boolean = false,
    description: String? = null,
    enabled: Boolean = true,
    textStyle: TextStyle = TextStyle.Default,
    cursorBrush: Brush = SolidColor(Color.Black),
    onValueChange: (Item) -> Unit,
) {

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        BaseWidget(
            modifier.menuAnchor(),
            value,
            title,
            unit,
            required,
            errorMsg,
            isError,
            description,
            onValueChange = {},
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
                                expanded = !expanded
                            }
                        }
                    }
                },
            cursorBrush,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            items.forEach { item ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // 去除已选中的item
                            items.forEach {
                                it.isCheck.value = false
                            }
                            item.isCheck.value = true
                            onValueChange(item)
                            expanded = false
                        }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween

                ) {
                    Text(text = item.name)
                    RadioButton(selected = item.isCheck.value, onClick = null)
                }

                HorizontalDivider(
                    modifier = Modifier
                        .height(0.5.dp)
                        .fillMaxWidth(), color = Color.Black
                )
            }
        }
    }

}