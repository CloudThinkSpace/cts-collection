package space.think.cloud.cts.lib.network.model.response

data class ResponseProjectLayer(
    val id: Int,
    val name: String,
    val type: String,
    val format: String,
    val url: String,
    val project_id: String,
    val checked: Boolean,
)