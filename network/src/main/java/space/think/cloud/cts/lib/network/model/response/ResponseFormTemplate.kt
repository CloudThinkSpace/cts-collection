package space.think.cloud.cts.lib.network.model.response

data class ResponseFormTemplate(
    val id: String,
    val name: String,
    val title: String,
    val description: String,
    val version: String,
    val createdAt: String,
    val updatedAt: String,
    val content: String,
    var isDefault: Boolean = false
)