package space.think.cloud.cts.lib.ui.form

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * ClassName: MapBottomSheet
 * Description:
 * @date: 2022/10/13 19:37
 * @author: tanghy
 */
@Composable
fun MapBottomSheet(
    isSubmit: Boolean = true,
    onGaodeNavigation: () -> Unit,
    onBaiduNavigation: () -> Unit,
    onCollect: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, end = 10.dp, bottom = 50.dp),
        horizontalAlignment = Alignment.End
    ) {

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
            shape = RoundedCornerShape(10.dp),
            onClick = onGaodeNavigation
        ) {
            Text(text = "高德导航", fontSize = MaterialTheme.typography.titleMedium.fontSize, color = Color.White)
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
            shape = RoundedCornerShape(10.dp),
            onClick = onBaiduNavigation
        ) {
            Text(text = "百度导航", fontSize = MaterialTheme.typography.titleMedium.fontSize, color = Color.White)
        }

        Spacer(modifier = Modifier.height(15.dp))

        if (isSubmit) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                ),
                shape = RoundedCornerShape(10.dp),
                onClick = onCollect
            ) {
                Text(text = "数据采集", fontSize = MaterialTheme.typography.titleMedium.fontSize, color = Color.White)
            }
        }

    }

}