package space.think.cloud.cts.lib.network.services

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import space.think.cloud.cts.lib.network.Constants
import space.think.cloud.cts.lib.network.model.request.RequestTask
import space.think.cloud.cts.lib.network.model.response.ResponsePage
import space.think.cloud.cts.lib.network.model.response.Result

interface TaskService {
    @POST("${Constants.TAG_API}/cts/task/search/{id}")
    suspend fun search(
        @Body post: RequestTask,
        @Path("id") dataTableName:String,
    ): Result<ResponsePage<Map<String,Any>>>
}