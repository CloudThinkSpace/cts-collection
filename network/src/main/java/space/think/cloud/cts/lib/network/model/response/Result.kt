package space.think.cloud.cts.lib.network.model.response

data class Result<T>(
    val code: Int,
    val msg: String,
    val data: T
)