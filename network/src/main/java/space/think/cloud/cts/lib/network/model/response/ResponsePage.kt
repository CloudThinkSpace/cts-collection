package space.think.cloud.cts.lib.network.model.response

data class ResponsePage<T>(
    val total: Int,
    val pages: Int,
    val pageNo: Int,
    val data: List<T>
)