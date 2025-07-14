package space.think.cloud.cts.lib.network.model.response

class ResponseTask(
    data: Map<String, Any?>
) : BaseData(data) {
    val tag: String = if (data.containsKey("状态")) {
        val status: String = data["状态"].toString();
        if (status == "1") "采农" else "未采"
    } else "未采"
}
