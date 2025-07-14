package space.think.cloud.cts.lib.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp

@Composable
fun SearchAppBar(
    searchValue: String,
    updateValue: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .padding(start = 10.dp, top = 10.dp, bottom = 10.dp, end = 10.dp)
                .weight(1f)
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
            )
        }

    }

}