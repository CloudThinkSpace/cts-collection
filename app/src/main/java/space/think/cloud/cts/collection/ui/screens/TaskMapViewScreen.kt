package space.think.cloud.cts.collection.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import org.maplibre.android.geometry.LatLng
import space.think.cloud.cts.collection.R
import space.think.cloud.cts.collection.viewmodel.TaskViewModel
import space.think.cloud.cts.common.gis.CtsMarker
import space.think.cloud.cts.common.gis.MapLibreMapController
import space.think.cloud.cts.common.gis.MapLibreMapView
import space.think.cloud.cts.lib.ui.task.TaskItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskMapViewScreen(
    dataTableName: String,
    taskItem: TaskItem?,
    taskViewModel: TaskViewModel = viewModel(key = "taskMap"),
    onBack: () -> Unit,
) {

    // 获取任务列表
    val taskList by taskViewModel.data.collectAsState()

    var mapLibreMapController: MapLibreMapController? by remember {
        mutableStateOf(null)
    }

    // 监听返回键
    BackHandler(enabled = true) {
        taskViewModel.reset()
        onBack()
    }

    LaunchedEffect(taskList) {
        if (taskList.isNotEmpty()) {
            val bounds = mutableListOf<LatLng>()
            // 任务标注
            taskList.forEach { taskItem ->
                val ctsMarker = CtsMarker(
                    lon = taskItem.lon.toDouble(),
                    lat = taskItem.lat.toDouble(),
                    title = taskItem.name,
                    description = "",
                    icon = if (taskItem.status == 0) R.drawable.location_blue else R.drawable.location_red
                )
                bounds.add(LatLng(ctsMarker.lat, ctsMarker.lon))
                mapLibreMapController?.addMarker(ctsMarker)
            }
            if (taskItem == null)
            // 缩放到地图
                mapLibreMapController?.animateToBounds(bounds)

        }

    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = {
                    Text("任务地图")
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
                actions = {
                    IconButton(onClick = {

                    }) {
                        Icon(
                            imageVector = Icons.Filled.Map,
                            contentDescription = "Localized description",
                            tint = Color.White
                        )
                    }
                },
            )
        }
    ) { paddingValues ->
        MapLibreMapView(
            modifier = Modifier.padding(paddingValues)
        ) {
            mapLibreMapController = it
            // 如果任务不为空，定位到任务点位处
            taskItem?.apply {
                // 移动到点位
                it.animateToLatLng(LatLng(this.lat.toDouble(), this.lon.toDouble())) {
                    val ctsMarker = CtsMarker(
                        lon = taskItem.lon.toDouble(),
                        lat = taskItem.lat.toDouble(),
                        title = taskItem.name,
                        description = "",
                        icon = if (taskItem.status == 0) R.drawable.location_blue else R.drawable.location_red
                    )
                    it.showInfoWindow(ctsMarker)
                }
            }
            // 查询所有任务
            taskViewModel.search(dataTableName, "")
        }
    }

}