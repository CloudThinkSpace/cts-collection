package space.think.cloud.cts.lib.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp


@Composable
fun LoginPage(
    modifier: Modifier,
    headerImg: Painter,
) {

    // 电话变量
    var photo by remember {
        mutableStateOf("")
    }
    // 密码变量
    var password by remember {
        mutableStateOf("")
    }
    // 是否显示密码
    var obscure by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = headerImg,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(5 / 4f)
        )
        Text("欢迎使用App", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = photo,
            onValueChange = { it ->
                photo = it
            },
            shape = MaterialTheme.shapes.small,
            leadingIcon = {
                Icon(Icons.Default.Phone, contentDescription = null)
            },
            label = {
                Text("手机号")
            }
        )
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = password,
            onValueChange = { it ->
                password = it
            },
            shape = MaterialTheme.shapes.small,
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = null)
            },
            label = {
                Text("密码")
            },
            trailingIcon = {
                val visibilityIcon = if (obscure) {
                    Icons.Filled.Visibility
                } else {
                    Icons.Filled.VisibilityOff
                }

                IconButton(
                    onClick = {
                        obscure = !obscure
                    }
                ) {
                    Icon(visibilityIcon, contentDescription = null)
                }

            }

        )
        Spacer(Modifier.height(16.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(),
            shape = RoundedCornerShape(10.dp),
            onClick = {

            }
        ) {
            Text("登录")
        }
    }
}
