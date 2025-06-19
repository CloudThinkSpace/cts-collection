package space.think.cloud.cts.lib.network.services

import retrofit2.http.Body
import retrofit2.http.POST
import space.think.cloud.cts.lib.network.model.request.RequestProjectSearch
import space.think.cloud.cts.lib.network.model.response.ResponseAuth
import space.think.cloud.cts.lib.network.model.response.ResponsePage
import space.think.cloud.cts.lib.network.model.response.ResponseProject
import space.think.cloud.cts.lib.network.model.response.Result

interface ProjectService {
    @POST("/cts/project/search")
    suspend fun search(@Body post: RequestProjectSearch): Result<ResponsePage<ResponseProject>>
}