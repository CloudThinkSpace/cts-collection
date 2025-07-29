package space.think.cloud.cts.collection.viewmodel

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import space.think.cloud.cts.common.gis.layer.Layer
import space.think.cloud.cts.lib.network.RetrofitClient
import space.think.cloud.cts.lib.network.services.ProjectLayerService

/**
 * <h1>在线请求项目图层处理类</h1>
 * ClassName: ProjectViewModel
 * Description:
 * @date: 2022/10/6 09:01
 * @author: tanghy
 */
class ProjectLayerViewModel : BaseViewModel() {

    private val _data = MutableStateFlow(listOf<Layer>())
    val data: StateFlow<List<Layer>> = _data.asStateFlow()

    private val projectLayerService = RetrofitClient.createService<ProjectLayerService>()

    /**
     * <h2>根据项目编号获取项目图层</h2>
     */
    fun getByProjectId(projectId: String, onResult: () -> Unit) = launch(
        {
            projectLayerService.getByProjectId(projectId)
        },
        onCallBack = onResult
    ) {
        it?.apply {
            _data.value = map { projectLayer ->
                Layer(
                    name = projectLayer.name,
                    type = projectLayer.type,
                    format = projectLayer.format,
                    url = projectLayer.url,
                    checked = mutableStateOf(projectLayer.checked)
                )
            }
        }
    }
}