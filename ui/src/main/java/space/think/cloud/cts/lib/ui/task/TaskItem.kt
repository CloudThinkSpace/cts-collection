package space.think.cloud.cts.lib.ui.task

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import space.think.cloud.cts.lib.ui.ActionIcon
import space.think.cloud.cts.lib.ui.LeadText

/**
 * ClassName: TaskItem
 * Description:
 * @date: 2022/11/1 17:40
 * @author: tanghy
 */
@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    leadText: String = "未采",
    title: String,
    lon: String,
    lat: String,
    status: Int = 0,
    leadBackgroundColor: Color = Color.Green,
    onClickDetail: (() -> Unit)? = null,
    onClick: () -> Unit,
) {

    Card(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(5.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    onClick()
                }
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                LeadText(
                    text = leadText,
                    backgroundColor = leadBackgroundColor
                )
                Column(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = title,
                            style = typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = lon,
                            style = typography.labelSmall,
                            color = Color.Gray
                        )

                        Text(
                            text = lat,
                            style = typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }
                if (status == 1) {
                    ActionIcon(onClick = {
                        onClickDetail?.invoke()
                    }, pointer = Icons.AutoMirrored.Filled.Assignment)
                }
            }
        }
    }
}