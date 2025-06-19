package space.think.cloud.cts.lib.ui.project

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import space.think.cloud.cts.lib.ui.CircleText

/**
 * ClassName: ProjectItem
 * Description:
 * @date: 2022/10/1 15:26
 * @author: tanghy
 */
@Composable
fun ProjectItem(
    modifier: Modifier = Modifier,
    title: String,
    subTitle: String,
    status: Int,
    type: Int,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 5.dp)
    ) {
        Card(
            modifier = modifier
                .fillMaxSize(),
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

                Row {
                    Image(
                        if (status == 0) Icons.AutoMirrored.Filled.ViewList else Icons.Default.LockClock,
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(
                            if (status == 0) MaterialTheme.colorScheme.primary else Color(0xFFDF3F3F)
                        ),
                        modifier = Modifier.size(40.dp)
                    )
                    Column(modifier = Modifier.padding(horizontal = 10.dp)) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = subTitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = if (type == 0) "任务型" else "自采型",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }

                IconButton(onClick = {

                }) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "进入项目",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

        }

        CircleText(
            modifier = Modifier.offset((-5).dp, (-5).dp),
            text = if (type == 0) "普" else "自",
            backgroundColor = if (type == 0) MaterialTheme.colorScheme.primary else Color.Blue
        )
    }


}