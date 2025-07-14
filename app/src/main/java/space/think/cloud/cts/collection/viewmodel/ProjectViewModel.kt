package space.think.cloud.cts.collection.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import space.think.cloud.cts.lib.network.RetrofitClient
import space.think.cloud.cts.lib.network.model.request.RequestProjectSearch
import space.think.cloud.cts.lib.network.services.ProjectService
import space.think.cloud.cts.lib.ui.project.ProjectData

class ProjectViewModel : BaseViewModel() {

    private val _posts = MutableStateFlow<List<ProjectData>>(emptyList())
    val data: StateFlow<List<ProjectData>> get() = _posts

    private val projectService = RetrofitClient.createService<ProjectService>()

    fun search(name: String) = launch(
        {
            projectService.search(RequestProjectSearch(name = name))
        }
    ) {
        it?.let { page ->
            val list = page.data
            _posts.value = list.map {
                // id字段绑定项目的数据编号，不是项目编号
                ProjectData(
                    id = it.dataTableName,
                    title = it.name,
                    subTitle = it.code,
                    type = it.type,
                    status = it.status
                )
            }
        }
    }

    override fun reset() {
        super.reset()
        _posts.value = emptyList()
    }

}
