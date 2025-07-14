package space.think.cloud.cts.lib.network.services

import retrofit2.http.Body
import retrofit2.http.POST
import space.think.cloud.cts.lib.network.Constants
import space.think.cloud.cts.lib.network.model.request.RequestLogin
import space.think.cloud.cts.lib.network.model.response.ResponseAuth
import space.think.cloud.cts.lib.network.model.response.Result

interface AuthService {
    @POST("${Constants.TAG_API}/login")
    suspend fun login(@Body post: RequestLogin): Result<ResponseAuth>
}