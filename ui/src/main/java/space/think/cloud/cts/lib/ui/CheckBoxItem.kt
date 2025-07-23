package space.think.cloud.cts.lib.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


@Composable
fun CheckBoxItem(
    modifier: Modifier = Modifier,
    imageVector: ImageVector = Icons.Default.Layers,
    text: String,
    textColor: Color = Color.Gray,
    checked:Boolean,
    onCheckedChange:(Boolean) -> Unit
) {

    Row(
        modifier = modifier.fillMaxWidth().clickable {
            onCheckedChange(!checked)
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = imageVector, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.padding(horizontal = 10.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = textColor
            )
        }

//        Switch(checked = checked, onCheckedChange = onCheckedChange)
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
    }

}