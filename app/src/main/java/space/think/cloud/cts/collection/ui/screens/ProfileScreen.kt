package space.think.cloud.cts.collection.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Abc
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.first
import space.think.cloud.cts.collection.viewmodel.AuthViewModel
import space.think.cloud.cts.common_datastore.DataStoreUtil
import space.think.cloud.cts.common_datastore.PreferencesKeys
import space.think.cloud.cts.collection.R;

/**
 * ClassName: HomeScreen
 * Description:
 * @date: 2022/10/1 13:12
 * @author: tanghy
 */

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel(),
    login:()-> Unit,
) {

    var nicename by remember {
        mutableStateOf("游客")
    }

    var phone by remember {
        mutableStateOf("暂无电话")
    }

    var isLogin by remember {
        mutableStateOf(true)
    }

    val context = LocalContext.current

    var isShow by remember {
        mutableStateOf(false)
    }

    if (isShow) {
        AlertDialog(
            onDismissRequest = {
                isShow = false
            },
            shape = RoundedCornerShape(5.dp),
            title = { Text(text = "提示信息", color = Color.Red) },
            text = { Text(text = "是否退出？") },
            confirmButton = {
                TextButton(onClick = {
                    isShow = false
                    viewModel.logout()
                    isLogin = false
                    nicename = "游客"
                    phone = "暂无电话"
                }) {
                    Text(text = "确定")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    isShow = false
                }) {
                    Text(text = "取消")
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        val baseStore = DataStoreUtil(context)

        baseStore.getData(PreferencesKeys.NICKNAME_KEY, "").first().also {
            isLogin = it.isNotEmpty()
        }.let {
            if (it.isNotEmpty()) {
                nicename = it
            }
        }

        baseStore.getData(PreferencesKeys.PHOTO_KEY, "").first().let {
            if (it.isNotEmpty()){
                phone = it
            }
        }

    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Image(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    painter = painterResource(R.drawable.logo_header),
                    contentScale = ContentScale.Crop,
                    contentDescription = ""
                )

                Column(modifier = Modifier.padding(10.dp)) {

                    Text(
                        text = nicename,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = phone,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White
                    )

                }
            }

            if (isLogin) {
                Button(onClick = {
                    isShow = true
                }) {
                    Text(text = "退出")
                }
            }

        }


        // 设置，关于，帮助
        Column(
            modifier = Modifier
                .weight(3f)
                .fillMaxSize()
        ) {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 5.dp
                ),
                shape = RoundedCornerShape(5.dp)
            ) {

                Row(
                    modifier = Modifier
                        .clickable {

                        }
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "设置",
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "设置", color = Color.Black)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = "", tint = Color.Black)
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(),
                    thickness = 0.5.dp, color = Color.Gray
                )

                Row(
                    modifier = Modifier
                        .clickable {

                        }
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Abc, contentDescription = "关于", tint = Color.Black)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "关于", color = Color.Black)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = "", tint = Color.Black)
                }

                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth(),
                    thickness = 0.5.dp, color = Color.Gray
                )

                Row(
                    modifier = Modifier
                        .clickable {

                        }
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.AutoMirrored.Default.Help,
                            contentDescription = "帮助",
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = "帮助", color = Color.Black)
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = "", tint = Color.Black)
                }

            }

            if (!isLogin) {
                // 登录登出
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .height(40.dp),

                    enabled = true,
                    shape = RoundedCornerShape(10.dp),
                    onClick = {
                        login()
                    }) {
                    Text(text = "登录", color = Color.White)
                }

            }
        }

    }
}