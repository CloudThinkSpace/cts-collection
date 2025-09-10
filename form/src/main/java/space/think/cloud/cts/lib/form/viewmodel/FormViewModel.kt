package space.think.cloud.cts.lib.form.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import space.think.cloud.cts.common_datastore.FormFieldDataStore
import space.think.cloud.cts.lib.form.FormField
import space.think.cloud.cts.lib.form.QuestionType
import space.think.cloud.cts.lib.form.utils.FileUploadUtils
import space.think.cloud.cts.lib.network.Constants
import space.think.cloud.cts.lib.network.RetrofitClient
import space.think.cloud.cts.lib.network.model.request.RequestFormData
import space.think.cloud.cts.lib.network.model.response.ResponseFormTemplate
import space.think.cloud.cts.lib.network.model.response.Result
import space.think.cloud.cts.lib.network.services.FormService
import space.think.cloud.cts.lib.network.services.FormTemplateService
import space.think.cloud.cts.lib.network.services.TaskService
import space.think.cloud.cts.lib.ui.form.MediaItem
import space.think.cloud.cts.lib.ui.project.ProjectData
import space.think.cloud.cts.lib.ui.utils.StringUtil

class FormViewModel : ViewModel() {


    private val _fields = mutableStateListOf<FormField>()
    val fields: List<FormField> = _fields

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    // 是否更新数据
    var isUpdate by mutableStateOf(false)

    private val taskData: MutableMap<String, Any> = mutableMapOf()

    // 表单模板操作接口
    private val formTemplateService = RetrofitClient.createService<FormTemplateService>()

    // 表单操作接口
    private val formService = RetrofitClient.createService<FormService>()

    // 任务接口
    private val taskService = RetrofitClient.createService<TaskService>()


    fun setTaskData(task: Map<String, Any>) {
        taskData.apply {
            clear()
            putAll(task)
        }
    }


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

    // 保存缓存数据
    suspend fun saveToDataStore(dataStore: FormFieldDataStore) {
        fields.forEach { formField ->
            if (formField.cached) {
                dataStore.save(formField.name, formField.value)
            }
        }
    }

    suspend fun submitData(
        context: Context,
        taskId: String,
        project: ProjectData,
        onResult: () -> Unit
    ) {
        // 1、查看是否有多媒体文件，如果有上传文件
        val uploadValue = getMediaItemsMap()
        // 上传文件
        uploadFile(context, uploadValue) { mapMap ->
            // 获取填报数据
            val result = getResult()
            // 重新替换照片相关字段值
            for ((key, value) in mapMap) {
                result[key] = StringUtil.mapToString(value)
            }
            // 设置常用字段
            result[Constants.Form.LAT] = taskData[Constants.Form.LAT].toString()
            result[Constants.Form.LON] = taskData[Constants.Form.LON].toString()
            result[Constants.Form.CODE] = taskData[Constants.Form.CODE].toString()
            result[Constants.Form.STATUS] = "0"
            // 提交或更新数据
            val param = RequestFormData(
                taskId = taskId,
                name = project.dataTableName,
                data = result
            )
            if (!isUpdate) {
                // 提交数据
                createFromData(param, onResult)
            } else {
                // 更新数据
                updateFormData(param, onResult)
            }
        }
    }

    private suspend fun uploadFile(
        context: Context,
        imageMaps: Map<String, Map<Int, MediaItem>>,
        onResult: (Map<String, Map<Int, MediaItem>>) -> Unit
    ) {
        val result = mutableMapOf<String, Map<Int, MediaItem>>()
        // 遍历不同问题的照片map对象
        for ((key, value) in imageMaps) {

            val imageMap = mutableMapOf<Int, MediaItem>()
            // 遍历问题待上传的图片
            for ((keyInt, imageItem) in value) {
                // 判断是否包含http，包含代表已经上传不需重复上传
                if (!imageItem.path.isNullOrEmpty() &&
                    !imageItem.path!!.contains("http")
                ) {
                    val response = try {
                        val formData =
                            FileUploadUtils.createImagePart(context, imageItem.path!!.toUri())
                        // 上传文件
                        formData?.let {
                            formService.uploadFile(formData)
                        }

                    } catch (_: Exception) {
                        Result(code = 500, msg = "上传文件失败")
                    }

                    if (response?.code != 200) {
                        _error.value = "上传文件失败"
                    }

                    response?.data?.let {

                        if (it.isNotEmpty()) {
                            val url = it[0].path
                            // 重新组装图片路径
                            imageItem.path = "${RetrofitClient.getBaseUrl()}api/image${url}"
                        }
                    }
                }
                // 收集imageItem数据
                imageMap[keyInt] = imageItem
            }
            // 收集imageMap数据
            result[key] = imageMap

        }

        onResult(result)
    }

    private fun getMediaItemsMap(): Map<String, Map<Int, MediaItem>> {
        val data = fields.filter { formField ->
            formField.type == QuestionType.ImageType.type || formField.type == QuestionType.VideoType.type
        }.associate {
            it.name to StringUtil.jsonToMap(it.value)
        }
        return data
    }

    private fun getResult(): MutableMap<String, String> {
        return fields.filter {
            it.type != QuestionType.SectionType.type
        }.associate {
            it.name to it.value
        }.toMutableMap()
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
    fun createFromData(formData: RequestFormData, onResult: () -> Unit) =
        launch(
            { formService.createFromData(formData) },
            {
                _error.value = it
            }
        ) {
            onResult()
        }

    /**
     * <h2>更新表单数据</h2>
     */
    fun updateFormData(formData: RequestFormData, onResult: () -> Unit) =
        launch(
            { formService.updateFormData(formData) },
            {
                _error.value = it
            }
        ) {
            onResult()
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
        block: suspend () -> Result<T>,
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
                result.msg?.apply {
                    errorFun?.invoke(this)
                    _error.value = this
                }
            } else {
                successFun(result.data)
            }
        }

    fun reset() {
        _error.value = null
        _fields.clear()
        isUpdate = false
        taskData.clear()
    }

}