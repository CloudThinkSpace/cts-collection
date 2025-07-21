package space.think.cloud.cts.lib.network.services

import retrofit2.http.POST
import retrofit2.http.Path
import space.think.cloud.cts.lib.network.Constants
import space.think.cloud.cts.lib.network.model.response.ResponseProjectLayer
import space.think.cloud.cts.lib.network.model.response.Result

/**
 *
 * ClassName: ApiService
 * Description: 网络请求服务
 * @date: 2022/10/30 19:37
 * @author: tanghy
 */
interface ProjectLayerService {


    // ----------------------------------start 项目图层操作接口--------------------------------------------------------

    @POST("${Constants.TAG_API}/cts/projectLayer/layers/{projectId}")
    suspend fun getByProjectId(
        @Path("projectId") id:String,
    ): Result<List<ResponseProjectLayer>>

    // ----------------------------------end 项目图层操作接口--------------------------------------------------------


}