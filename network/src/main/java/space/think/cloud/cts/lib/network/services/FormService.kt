package space.think.cloud.cts.lib.network.services

import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import space.think.cloud.cts.lib.network.Constants
import space.think.cloud.cts.lib.network.model.request.RequestFormData
import space.think.cloud.cts.lib.network.model.response.ResponseUploadFile
import space.think.cloud.cts.lib.network.model.response.Result

interface FormService {

    @POST("${Constants.TAG_API}/cts/form/addForm")
    suspend fun createFromData(
        @Body formData: RequestFormData
    ): Result<Any?>

    @POST("${Constants.TAG_API}/cts/form/updateForm")
    suspend fun updateFormData(
        @Body formData: RequestFormData
    ): Result<Any?>

    @GET("${Constants.TAG_API}/cts/form/query/{tableId}/{id}")
    suspend fun getByFormDataId(
        @Path("tableId") tableId: String,
        @Path("id") id: String,
    ): Result<Map<String, Any>?>

    @Multipart
    @POST("${Constants.TAG_API}/sys/upload")
    suspend fun uploadFile(
        @Part photo: MultipartBody.Part
    ): Result<List<ResponseUploadFile>>
}