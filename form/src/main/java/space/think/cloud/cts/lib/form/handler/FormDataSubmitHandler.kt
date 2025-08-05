package space.think.cloud.cts.lib.form.handler

import space.think.cloud.cts.lib.form.viewmodel.FormViewModel
import space.think.cloud.cts.lib.network.model.request.RequestFormData

/**
 * ClassName: FormDataSubmitHandler
 * Description:
 * @date: 2022/11/2 15:28
 * @author: tanghy
 */
class FormDataSubmitHandler(
    private val taskId: String,
    private val taskTableName: String,
    private val result: Map<String, Any?>,
    private val isUpdate: Boolean = false,
    private val formDataViewModel: FormViewModel
) {

    fun handler() {
        // 提交数据
        val param = RequestFormData(
            taskId = taskId,
            name = taskTableName,
            data = result
        )
        if (!isUpdate) {
            // 提交数据
            formDataViewModel.createFromData(param)
        } else {
            // 更新数据
            formDataViewModel.updateFormData(param)
        }
    }


}