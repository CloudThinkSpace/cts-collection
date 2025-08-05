package space.think.cloud.cts.lib.form.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
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


    /**
     * 查询表单数据
     */
    fun queryFormTemplateById(
        formTemplateId: String,
        onSuccess: (ResponseFormTemplate) -> Unit,
    ) = launch(
        {
            formTemplateService.getByTemplateId(formTemplateId)
        },
        {
            _error.value = it
        }
    ) {
        it?.let { formTemplate ->
            onSuccess(formTemplate)
        }
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

    fun  updateFields(fields: List<FormField>){
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