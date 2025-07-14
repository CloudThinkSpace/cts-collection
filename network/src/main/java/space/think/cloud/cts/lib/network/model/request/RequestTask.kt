package space.think.cloud.cts.lib.network.model.request

data class RequestTask(
    val page: RequestPage,
    val fields: List<String>,
    val wheres: List<String>
)