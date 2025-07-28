package space.think.cloud.cts.collection.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import space.think.cloud.cts.collection.datastore.DataStoreManage
import space.think.cloud.cts.collection.viewmodel.ProjectViewModel
import space.think.cloud.cts.lib.ui.SearchAppBar
import space.think.cloud.cts.lib.ui.project.ProjectContent
import space.think.cloud.cts.lib.ui.project.ProjectData


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectScreen(
    modifier: Modifier,
    projectViewModel: ProjectViewModel = viewModel(),
    onClick: (ProjectData) -> Unit
) {

    // 获取项目列表
    val projectList by projectViewModel.data.collectAsState()

    var searchValue by remember { mutableStateOf("") }

    var isLogin by remember {
        mutableStateOf(false)
    }
    var checkToken by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(searchValue) {
        // 请求项目列表
        if (isLogin)
            projectViewModel.search(searchValue)
    }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val dataStoreManage = DataStoreManage(context = context)
        val token = dataStoreManage.getToken()
        checkToken = false
        if (token.isNotEmpty()) {
            isLogin = true
            projectViewModel.search(searchValue)
        }else{
            projectViewModel.reset()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        SearchAppBar(
            modifier = Modifier.padding(10.dp),
            searchValue = searchValue,
            onClear = {
                searchValue = ""
            }
        ) {
            searchValue = it
        }
        if (isLogin) {
            ProjectContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                items = projectList,
                onClick = onClick,
            )
        } else {
            if (checkToken == true) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "请先登录后查询项目...",
                        color = Color.Gray,
                        fontSize = MaterialTheme.typography.titleMedium.fontSize
                    )
                }
            }
        }


    }

}