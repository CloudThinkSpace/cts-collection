package space.think.cloud.cts.lib.network.model.response

data class ResponseProject(
    val id: String,
    val name: String,
    val code: String,
    val type: Int,
    val status: Int,
    val total: Int,
    val formTemplate: String?,
    val formTemplateName: String,
    val dataTableName: String,
    val description: String?,
    val remark: String?,
    val createdAt: String,
    val updatedAt: String?,
)
