package space.think.cloud.cts.lib.ui.form.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * ClassName: SectionWidget
 * Description:
 * @date: 2022/10/16 14:35
 * @author: tanghy
 */
@Composable
fun SectionWidget(
    modifier: Modifier = Modifier,
    title: String,
) {
    Column(
        modifier
            .fillMaxWidth()
            .padding(10.dp, 20.dp, 10.dp, 10.dp)
    ) {
        Text(text = title, fontSize = MaterialTheme.typography.titleLarge.fontSize, fontWeight = FontWeight.Bold)
        HorizontalDivider(
            modifier = Modifier.padding(0.dp, 5.dp, 0.dp, 0.dp),
            thickness = Dp.Hairline
        )
    }
}