package space.think.cloud.cts.collection.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import space.think.cloud.cts.lib.network.RetrofitClient
import space.think.cloud.cts.lib.network.model.request.RequestPage
import space.think.cloud.cts.lib.network.model.request.RequestProjectSearch
import space.think.cloud.cts.lib.network.model.response.ResponseProject

class ProjectViewModel : BaseViewModel() {

    private val _posts = MutableStateFlow<List<ResponseProject>>(emptyList())
    val data: StateFlow<List<ResponseProject>> get() = _posts

    private val projectService = RetrofitClient.projectService

    init {
        search()
    }

    fun search() {
        viewModelScope.launch {
            try {
                val response = projectService.search(RequestProjectSearch(page = RequestPage()))
//                _posts.value = response.data.data
                _posts.value = listOf(ResponseProject(
                    id = "1",
                    name = "",
                    code = "",
                    type = 0,
                    status = 0,
                    total = 1,
                    formTemplate = "",
                    formTemplateName = "",
                    dataTableName = "",
                    description = "",
                    remark = "",
                    createdAt = "",
                    updatedAt = ""
                ))
            } catch (e: Exception) {
                _error.value = e.toString()
            }
        }
    }

}
