package space.think.cloud.cts.collection.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import space.think.cloud.cts.lib.ui.R


@Composable
fun LoginScreen(
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
    // 是否显示进度
    var loading by remember {
        mutableStateOf(false)
    }
    // 信息展示
    val snackbarHostState = remember {
        SnackbarHostState()
    }
    // 线程
    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->


        Column(
            modifier = modifier
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box {
                Image(
                    painter = painterResource(R.drawable.shape),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(5 / 5f)
                )
                Column(
                    modifier = Modifier.matchParentSize().padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround
                ) {
                    Image(
                        painter = headerImg,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                    Text("欢迎使用", style = MaterialTheme.typography.headlineMedium)
                }
            }

            Column(
                modifier = Modifier.padding(28.dp, 0.dp)
            ) {

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = photo,
                    readOnly = loading,
                    onValueChange = { it ->
                        photo = it
                    },
                    shape = MaterialTheme.shapes.small,
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = null)
                    },
                    label = {
                        Text("手机号")
                    },
                    singleLine = true,
                    trailingIcon = {
                        // 判断是否为空
                        if (photo.isNotEmpty() && !loading) {
                            IconButton(onClick = { photo = "" }) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = null
                                )
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    )
                )

                Spacer(Modifier.height(10.dp))

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(0.dp),
                    value = password,
                    readOnly = loading,
                    singleLine = true,
                    onValueChange = { it ->
                        password = it
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    shape = MaterialTheme.shapes.small,
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null)
                    },
                    visualTransformation = if (obscure) VisualTransformation.None else PasswordVisualTransformation(),
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
                    enabled = !loading,
                    onClick = {
                        if (photo.isEmpty() || password.isEmpty()) {
                            scope.launch {
                                snackbarHostState.showSnackbar("手机号或者密码不能为空")
                            }
                        } else {
                            loading = !loading
                        }
                    }
                ) {
                    Text("登录")
                }

                if (loading) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .height(1.dp)
                            .fillMaxWidth()
                    )
                }

            }

        }
    }


}
