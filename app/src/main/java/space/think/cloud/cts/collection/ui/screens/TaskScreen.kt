package space.think.cloud.cts.collection.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import space.think.cloud.cts.collection.viewmodel.TaskViewModel
import space.think.cloud.cts.lib.ui.SearchAppBar
import space.think.cloud.cts.lib.ui.project.ProjectData
import space.think.cloud.cts.lib.ui.task.TaskContent
import space.think.cloud.cts.lib.ui.task.TaskItem


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreen(
    modifier: Modifier = Modifier,
    project: ProjectData,
    taskViewModel: TaskViewModel = viewModel(key = "taskList"),
    onBack: () -> Unit,
    onClickDetail: ((TaskItem) -> Unit)? = null,
    onClick: (TaskItem?) -> Unit
) {

    // 获取任务列表
    val taskList by taskViewModel.data.collectAsState()
    // 加载中
    var isLoading by remember { mutableStateOf(true) }

    var searchValue by remember { mutableStateOf("") }
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("全部", "未采", "已采")

    LaunchedEffect(searchValue, selectedTabIndex) {
        isLoading = true
        taskViewModel.search(project.dataTableName, searchValue, selectedTabIndex) {
            isLoading = false
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SearchAppBar(
                            modifier = Modifier,
                            searchValue = searchValue,
                            placeholder = "点位搜索",
                            onClear = {
                                searchValue = ""
                            }
                        ) {
                            searchValue = it
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        taskViewModel.reset()
                        onBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description",
                            tint = Color.White
                        )
                    }
                },
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier.padding(paddingValues)
        ) {

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                indicator = { tabPositions ->
                    if (selectedTabIndex < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = Color(0xFF6A87E7)
                        )
                    }
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                title,
                                color = if (selectedTabIndex == index) Color(0xFF2196F3) else Color.White
                            )
                        },
                    )
                }
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                TaskContent(
                    taskList = taskList,
                    onClick = {
                        onClick(it)
                    },
                    onClickDetail = onClickDetail,
                )
            }
        }
    }

}