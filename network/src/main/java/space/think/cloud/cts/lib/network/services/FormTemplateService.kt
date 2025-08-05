package space.think.cloud.cts.lib.network.services

import retrofit2.http.GET
import retrofit2.http.Path
import space.think.cloud.cts.lib.network.Constants
import space.think.cloud.cts.lib.network.model.response.ResponseFormTemplate
import space.think.cloud.cts.lib.network.model.response.Result

interface FormTemplateService {

    @GET("${Constants.TAG_API}/cts/form/template/query/{id}")
    suspend fun getByTemplateId(
        @Path("id") id: String,
    ): Result<ResponseFormTemplate?>

}