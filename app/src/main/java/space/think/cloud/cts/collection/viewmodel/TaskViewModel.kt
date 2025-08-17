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

    /**
     * 查询任务表
     * @param dataTableName 任务表
     * @param searchValue 查询条件
     * @param status 查询状态
     */
    fun search(dataTableName: String, searchValue: String, status:Int = 0, onResult: () -> Unit) = launch(
        {
            taskService.search(
                RequestTask(
                    page = RequestPage(),
                    fields = listOf("code", "lon", "lat", "status", "id"),
                    wheres = when(status) {
                        1 -> {
                            listOf("code like '%${searchValue}%'", "status = 0")
                        }
                        2 -> {
                            listOf("code like '%${searchValue}%'", "status = 1")
                        }
                        else -> {
                            listOf("code like '%${searchValue}%'")
                        }
                    }
                ),
                dataTableName = dataTableName,
            )
        },
        onCallBack = {
            onResult()
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
