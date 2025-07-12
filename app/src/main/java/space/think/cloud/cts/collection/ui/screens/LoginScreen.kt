package space.think.cloud.cts.collection.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SaveAs
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import space.think.cloud.cts.collection.viewmodel.AuthViewModel
import space.think.cloud.cts.collection.R


/**
 * ClassName: LoginScreen
 * Description:
 * @date: 2022/9/30 20:33
 * @author: tanghy
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(),
    onBack: ()-> Unit,
) {

    // 用户变量
    var username by remember {
        mutableStateOf("")
    }

    // 密码变量
    var password by remember {
        mutableStateOf("")
    }

    // 是否显示密码变量
    var isPasswordVisible by remember {
        mutableStateOf(false)
    }

    // 是否显示加载组件的变量
    var isLoading by remember {
        mutableStateOf(false)
    }

    val context = LocalContext.current

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        "登录",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) {
        Box(
            modifier = Modifier.padding(it).fillMaxSize(),
            contentAlignment = Alignment.TopEnd
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                Image(
                    modifier = Modifier
                        .weight(1f)
                        .padding(10.dp)
                        .clip(CircleShape),

                    contentScale = ContentScale.FillBounds,
                    painter = painterResource(R.drawable.logo_header),
                    contentDescription = "Logo"
                )

                Card(
                    modifier = Modifier
                        .weight(2f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(topStart = 100f, topEnd = 100f),

                    ) {


                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 30.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "欢迎使用",
                            color = Color.Gray
                        )
                        Text(
                            text = "外业核查采集App",
                            fontWeight = FontWeight.Bold,
                            fontSize = MaterialTheme.typography.headlineLarge.fontSize,
                            color = Color.Gray
                        )

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {

                            // 用户组件
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = username,
                                onValueChange = {
                                    username = it
                                },
                                label = { Text(text = "用户") },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = "User")
                                },
                                trailingIcon = {
                                    IconButton(onClick = { username = "" }) {
                                        Icon(
                                            imageVector = Icons.Filled.Clear,
                                            contentDescription = ""
                                        )
                                    }
                                },

                                )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = password,
                                onValueChange = {
                                    password = it
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                label = { Text(text = "密码") },
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = "Lock")
                                },
                                trailingIcon = {
                                    IconButton(onClick = {
                                        isPasswordVisible = !isPasswordVisible
                                    }) {
                                        Icon(
                                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = ""
                                        )
                                    }
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                enabled = !isLoading,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                ),
                                shape = RoundedCornerShape(10.dp),
                                onClick = {
                                    isLoading = true
                                    // 登录
                                    viewModel.login(username, password) {

                                    }

                                }) {
                                Text(text = "登录", fontSize = 20.sp, color = Color.White)
                            }


                        }


                    }
                }

            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(10.dp)
                        .size(50.dp),
                    color = Color.White
                )
            }

            if (viewModel.data.collectAsState().value != null) {
                Toast.makeText(context, "登录成功", Toast.LENGTH_LONG).show()
                isLoading = false
                onBack()
            }



            if (viewModel.error.collectAsState().value != null) {
                Toast.makeText(context, viewModel.error.collectAsState().value, Toast.LENGTH_LONG)
                    .show()
                isLoading = false
            }

        }
    }

    Surface(color = MaterialTheme.colorScheme.primary) {



    }

}
