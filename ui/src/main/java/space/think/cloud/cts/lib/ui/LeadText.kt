package space.think.cloud.cts.lib.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

/**
 * ClassName: FileTypeText
 * Description:
 * @date: 2022/10/20 19:00
 * @author: tanghy
 */
@Composable
fun LeadText(
    modifier: Modifier = Modifier,
    text: String,
    size: Dp = 40.dp,
    fontSize: TextUnit = typography.labelSmall.fontSize,
    color: Color = Color.White,
    backgroundColor:Color = MaterialTheme.colorScheme.primary
) {

    Box(
        modifier = modifier
            .size(size)
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
            .padding(2.dp),
        contentAlignment = Alignment.Center
    ) {

        Text(text = text, fontSize = fontSize, color = color)

    }

}