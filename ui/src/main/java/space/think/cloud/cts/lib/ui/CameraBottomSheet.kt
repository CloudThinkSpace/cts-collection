package space.think.cloud.cts.lib.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
fun CameraBottomSheet(
    containerColor: Color = Color(0xFF5ac18e),
    onCamera: () -> Unit,
    onSelect: () -> Unit
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
                containerColor = containerColor,
            ),
            shape = RoundedCornerShape(10.dp),
            onClick = onCamera
        ) {
            Text(text = "拍照", fontSize = 20.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(15.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = containerColor,
            ),
            shape = RoundedCornerShape(10.dp),
            onClick = onSelect
        ) {
            Text(text = "选择", fontSize = 20.sp, color = Color.White)
        }

    }

}