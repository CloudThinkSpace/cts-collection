package space.think.cloud.cts.collection.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import space.think.cloud.cts.lib.network.RetrofitClient
import space.think.cloud.cts.lib.network.model.request.RequestPage
import space.think.cloud.cts.lib.network.model.request.RequestProjectSearch
import space.think.cloud.cts.lib.ui.project.ProjectData

class ProjectViewModel : BaseViewModel() {

    private val _posts = MutableStateFlow<List<ProjectData>>(emptyList())
    val data: StateFlow<List<ProjectData>> get() = _posts

    private val projectService = RetrofitClient.projectService

    fun search() = launch(
        {
            projectService.search(RequestProjectSearch(page = RequestPage()))
        }
    ) {
        it?.let { page ->
            val list = page.data
            _posts.value = list.map {
                ProjectData(
                    id = it.id,
                    title = it.name,
                    subTitle = it.code,
                    type = it.type,
                    status = it.status
                )
            }
        }
    }

}
