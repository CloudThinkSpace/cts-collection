package space.think.cloud.cts.lib.network.model.response

data class Result<T>(
    val code: Int = 200,
    val msg: String? = null,
    val data: T? = null
)