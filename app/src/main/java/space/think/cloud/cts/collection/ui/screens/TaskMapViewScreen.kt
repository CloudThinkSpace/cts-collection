@file:Suppress("DEPRECATION")

package space.think.cloud.cts.collection.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.maplibre.android.annotations.Marker
import org.maplibre.android.geometry.LatLng
import space.think.cloud.cts.collection.R
import space.think.cloud.cts.collection.viewmodel.TaskViewModel
import space.think.cloud.cts.common.gis.CtsMarker
import space.think.cloud.cts.common.gis.MapLibreMapController
import space.think.cloud.cts.common.gis.MapLibreMapView
import space.think.cloud.cts.common.gis.utils.MapNavigationUtil
import space.think.cloud.cts.common.gis.utils.TransformUtils
import space.think.cloud.cts.lib.ui.form.MapBottomSheet
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

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var currentMarker: Marker? by remember {
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
            taskList.forEach { it ->
                val ctsMarker = CtsMarker(
                    lon = it.lon.toDouble(),
                    lat = it.lat.toDouble(),
                    title = it.name,
                    description = "",
                    icon = if (it.status == 0) R.drawable.location_blue else R.drawable.location_red
                )
                bounds.add(LatLng(ctsMarker.lat, ctsMarker.lon))
                val marker: Marker? = mapLibreMapController?.addMarker(ctsMarker)
                if (it.code == taskItem?.code) {
                    currentMarker = marker

                }
            }
            if (taskItem == null) {
                // 缩放到地图
                mapLibreMapController?.animateToBounds(bounds)
            } else {

                currentMarker?.let { marker ->
                    // 移动到点位
                    mapLibreMapController?.animateToLatLng(
                        LatLng(
                            marker.position.latitude,
                            marker.position.longitude
                        ),
                        zoom = 14.0
                    ) {
                        mapLibreMapController?.showInfoWindow(marker)
                    }
                }
            }

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
            modifier = Modifier.padding(paddingValues),
            onInfoWindowClick = {
                currentMarker = it
                showBottomSheet = true
            }
        ) {
            mapLibreMapController = it
            // 查询所有任务
            taskViewModel.search(dataTableName, "")
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            shape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp),
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            MapBottomSheet(
                onGaodeNavigation = {
                    showBottomSheet = false

                    currentMarker?.apply {

                        // 坐标转换，转成高德坐标
                        val latLng = TransformUtils.wgs84ToGcj02(
                            this.position.longitude,
                            this.position.latitude
                        )
                        // 打开高德导航软件
                        MapNavigationUtil.gaodeIntent(
                            context,
                            latLng[1],
                            latLng[0]
                        )
                    }

                },
                onBaiduNavigation = {
                    showBottomSheet = false
                    currentMarker?.apply {
                        // 坐标转换，转成百度坐标
                        val latLng = TransformUtils.wgs84ToBd09(
                            this.position.longitude,
                            this.position.latitude
                        )
                        // 打开百度导航软件
                        MapNavigationUtil.baiduIntent(
                            context,
                            latLng[1],
                            latLng[0]
                        )
                    }

                }
            ) {
                // 打开表单页


            }
        }
    }

}