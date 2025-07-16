package space.think.cloud.cts.collection.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import space.think.cloud.cts.lib.network.RetrofitClient
import space.think.cloud.cts.lib.network.model.request.RequestPage
import space.think.cloud.cts.lib.network.model.request.RequestTask
import space.think.cloud.cts.lib.network.model.response.ResponseTask
import space.think.cloud.cts.lib.network.services.TaskService
import space.think.cloud.cts.lib.ui.task.TaskItem

class TaskViewModel : BaseViewModel() {

    private val _posts = MutableStateFlow<List<TaskItem>>(emptyList())
    val data: StateFlow<List<TaskItem>> get() = _posts

    private val taskService = RetrofitClient.createService<TaskService>()

    fun search(dataTableName: String, searchValue: String) = launch(
        {
            taskService.search(
                RequestTask(
                    page = RequestPage(),
                    fields = listOf("code", "lon", "lat", "status", "id"),
                    wheres = listOf("code like '%${searchValue}%'")
                ),
                dataTableName = dataTableName,
            )
        }
    ) {
        it?.let { page ->
            val list = page.data
            _posts.value = list.map {
                ResponseTask(it)
            }.map {
                TaskItem(
                    id = it.id,
                    name = it.code,
                    code = it.code,
                    lon = it.lon.toString(),
                    lat = it.lat.toString(),
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
