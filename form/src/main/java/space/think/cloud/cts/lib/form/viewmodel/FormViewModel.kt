package space.think.cloud.cts.lib.form.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import space.think.cloud.cts.lib.form.FormField
import space.think.cloud.cts.lib.network.RetrofitClient
import space.think.cloud.cts.lib.network.model.request.RequestFormData
import space.think.cloud.cts.lib.network.model.response.ResponseFormTemplate
import space.think.cloud.cts.lib.network.model.response.Result
import space.think.cloud.cts.lib.network.services.FormService
import space.think.cloud.cts.lib.network.services.FormTemplateService
import space.think.cloud.cts.lib.network.services.TaskService

class FormViewModel : ViewModel() {


    private val _fields = mutableStateListOf<FormField>()
    val fields: List<FormField> = _fields

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    // 是否提交数据
    var isSubmit by mutableStateOf(false)

    // 表单模板操作接口
    private val formTemplateService = RetrofitClient.createService<FormTemplateService>()

    // 表单操作接口
    private val formService = RetrofitClient.createService<FormService>()

    // 任务接口
    private val taskService = RetrofitClient.createService<TaskService>()


    fun getAllData(
        formTemplateId: String,
        tableName: String,
        taskId: String,
        onSuccess: (
            ResponseFormTemplate,
            Map<String, Any>,
            Map<String, Any>?
        ) -> Unit
    ) {
        viewModelScope.launch {

            try {
                val deferredFormTemplate = async { queryFormTemplateById(formTemplateId) }
                val deferredTask = async { queryTaskById(tableName, taskId) }
                val deferredFormData = async { queryFormDataByTaskId(tableName, taskId) }

                val result1 = deferredFormTemplate.await()
                val result2 = deferredTask.await()
                val result3 = deferredFormData.await()

                if (result1.code == 200 && result2.code == 200 && result3.code == 200) {
                    val formTemplate = result1.data
                    val task = result2.data
                    if (formTemplate == null || task == null) {
                        throw IllegalArgumentException("表单模板或者任务数据不能为空")
                    }
                    onSuccess(formTemplate, task, result3.data)
                } else {
                    throw IllegalArgumentException("数据请求数据失败，请检测网络")
                }
            } catch (e: Exception) {
                _error.value = e.toString()
            }

        }
    }


    /**
     * 查询表单数据
     */
    private suspend fun queryFormTemplateById(
        formTemplateId: String
    ): Result<ResponseFormTemplate?> {
        val result = formTemplateService.getByTemplateId(formTemplateId)
        return result
    }

    /**
     * 查询任务数据
     */
    private suspend fun queryTaskById(
        taskTableName: String,
        taskId: String
    ): Result<Map<String, Any>?> {
        val result = taskService.getByTaskId(
            taskName = taskTableName,
            id = taskId
        )
        return result
    }

    /**
     * 查询采集的数据
     */
    private suspend fun queryFormDataByTaskId(
        formTableName: String,
        code: String
    ): Result<Map<String, Any>?> {
        val result = formService.getByFormDataId(
            tableId = formTableName,
            id = code
        )
        return result
    }

    /**
     * <h2>创建表单数据</h2>
     */
    fun createFromData(formData: RequestFormData) =
        launch({ formService.createFromData(formData) }) {
            isSubmit = true
        }

    /**
     * <h2>更新表单数据</h2>
     */
    fun updateFormData(formData: RequestFormData) =
        launch({ formService.updateFormData(formData) }) {
            isSubmit = true
        }

    /**
     * 更新字段信息
     */
    fun updateField(updatedField: FormField) {
        // 根据id获取数组位置
        val index = _fields.indexOfFirst { it.id == updatedField.id }
        if (index != -1) {
            // 更新字段
            _fields[index] = updatedField  // 只更新特定项
        }
    }

    fun updateFields(fields: List<FormField>) {
        _fields.clear()
        _fields.addAll(fields)
    }

    private fun <T> launch(
        block: suspend () -> space.think.cloud.cts.lib.network.model.response.Result<T>,
        errorFun: ((String) -> Unit)? = null,
        onCallBack: (() -> Unit)? = null,
        successFun: (T?) -> Unit
    ) =
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    block()
                } catch (e: Exception) {
                    Result(500, e.toString(), null)
                }
            }
            onCallBack?.invoke()
            if (result.code != 200) {
                result.msg.apply {
                    errorFun?.invoke(this)
                    _error.value = this
                }
            } else {
                successFun(result.data)
            }
        }

    fun reset() {
        _error.value = null
    }

}