package space.think.cloud.cts.lib.network.model.request

data class RequestFormData(
    val taskId: String,
    val name: String,
    var data: Map<String, Any?>? = null
)