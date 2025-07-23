@file:Suppress("DEPRECATION")

package space.think.cloud.cts.collection.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.style.expressions.Expression
import org.maplibre.geojson.Feature
import space.think.cloud.cts.collection.R
import space.think.cloud.cts.collection.taskItemToFeature
import space.think.cloud.cts.collection.viewmodel.ProjectLayerViewModel
import space.think.cloud.cts.collection.viewmodel.TaskViewModel
import space.think.cloud.cts.common.gis.MapLibreMapController
import space.think.cloud.cts.common.gis.MapLibreMapView
import space.think.cloud.cts.common.gis.utils.MapNavigationUtil
import space.think.cloud.cts.common.gis.utils.TransformUtils
import space.think.cloud.cts.lib.ui.CheckBoxItem
import space.think.cloud.cts.lib.ui.form.MapBottomSheet
import space.think.cloud.cts.lib.ui.task.TaskItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskMapViewScreen(
    projectId: String,
    dataTableName: String,
    taskItem: TaskItem?,
    taskViewModel: TaskViewModel = viewModel(key = "taskMap"),
    projectLayerViewModel: ProjectLayerViewModel = viewModel(),
    onBack: () -> Unit,
) {

    // 获取任务列表
    val taskList by taskViewModel.data.collectAsState()

    val projectLayers by projectLayerViewModel.data.collectAsState()

    var mapLibreMapController: MapLibreMapController? by remember {
        mutableStateOf(null)
    }

    val operationSheetState = rememberModalBottomSheetState()
    val layerSheetState = rememberModalBottomSheetState()
    // 操作bottomSheet
    var operationBottomSheet by remember { mutableStateOf(false) }
    var layerBottomSheet by remember { mutableStateOf(false) }

    val context = LocalContext.current

    var currentMarker: Marker? by remember {
        mutableStateOf(null)
    }

    var loadingIndex by remember { mutableIntStateOf(0) }

    val scope = rememberCoroutineScope()

    // 监听返回键
    BackHandler(enabled = true) {
        taskViewModel.reset()
        onBack()
    }

    // 图层查询结果处理
    LaunchedEffect(projectLayers, taskList) {
        loadingIndex++
        if (loadingIndex == 3) {
            if (projectLayers.isNotEmpty()) {
                projectLayers.forEach {
                    mapLibreMapController?.addLayer(it.name, it.url)
                }
            }

            if (taskList.isNotEmpty()) {
                val bounds = mutableListOf<LatLng>()
                val features = mutableListOf<Feature>()
                // 任务标注
                taskList.forEach { it ->
                    val lat = it.lat.toDouble()
                    val lon = it.lon.toDouble()
                    // 收集坐标
                    bounds.add(LatLng(lat, lon))
                    if (it.code == taskItem?.code) {
                        currentMarker = Marker(MarkerOptions().apply {
                            this.position = LatLng(lat, lon)
                            title = it.code
                        })
                    }
                    val feature = taskItemToFeature(it)
                    features.add(feature)
                }
                // 添加默认样式图层
                mapLibreMapController?.addSymbolLayer(
                    expression = Expression.match(
                        Expression.get("status"),
                        Expression.literal("marker-blue"),
                        Expression.stop(0, "marker-blue"),
                        Expression.stop(1, "marker-red")
                    ),
                    features = features,
                )
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
                        scope.launch {
                            layerBottomSheet = true
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Layers,
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
                operationBottomSheet = true
            }
        ) {
            mapLibreMapController = it
            // 添加图标
            mapLibreMapController?.addImage("marker-blue", R.drawable.location_blue)
            mapLibreMapController?.addImage("marker-red", R.drawable.location_red)

            scope.launch {
                val deferred1 = async {
                    // 查询所有任务
                    taskViewModel.search(dataTableName, "")
                }
                val deferred2 = async {
                    projectLayerViewModel.getByProjectId(projectId) { }
                }
                // 等待两个协程都完成
                awaitAll(deferred1, deferred2)

            }
        }
    }

    if (operationBottomSheet) {
        ModalBottomSheet(
            shape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp),
            onDismissRequest = {
                operationBottomSheet = false
            },
            sheetState = operationSheetState
        ) {
            MapBottomSheet(
                onGaodeNavigation = {
                    operationBottomSheet = false

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
                    operationBottomSheet = false
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

    if (layerBottomSheet) {
        ModalBottomSheet(
            shape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp),
            onDismissRequest = {
                layerBottomSheet = false
            },
            sheetState = layerSheetState,
            dragHandle = {}
        ) {
            LazyColumn(
                modifier = Modifier
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp)
                    )
                    .fillMaxHeight(0.6f)
                    .padding(5.dp)
            ) {

                item {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "图层管理",
                            style = MaterialTheme.typography.titleMedium
                        )
                        IconButton(onClick = {
                            scope.launch {
                                layerSheetState.hide()
                            }.invokeOnCompletion {
                                if (!layerSheetState.isVisible) {
                                    layerBottomSheet = false
                                }
                            }
                        }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }
                    Divider(thickness = 0.5.dp)
                }

                itemsIndexed(
                    items = projectLayers
                )
                { _, item ->

                    CheckBoxItem(
                        text = item.name,
                        checked = item.checked.value,
                        onCheckedChange = {
                            item.checked.value = it
                            mapLibreMapController?.toggleLayer(item.name, it)
                        }
                    )

                }

            }
        }
    }

}