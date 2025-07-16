package space.think.cloud.cts.lib.ui.form

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
            Text(text = "高德导航", fontSize = 20.sp, color = Color.White)
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
            Text(text = "百度导航", fontSize = 20.sp, color = Color.White)
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
                Text(text = "数据采集", fontSize = 20.sp, color = Color.White)
            }
        }

    }

}