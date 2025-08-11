package space.think.cloud.cts.lib.network.geocode

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okio.IOException

/**
 * 天地图地理编码api
 */
fun geocodeGetRequest(lon: String, lat: String, token: String, callback: (String) -> Unit) {
    val client = OkHttpClient()
    val url =
        "http://api.tianditu.gov.cn/geocoder?{'lon':$lon,'lat':$lat,'ver':1}&type=geocode&tk=$token"

    val request = Request.Builder()
        .url(url)
        .get() // GET 方法（默认可不写）
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            // 处理网络错误
            e.printStackTrace()
        }

        override fun onResponse(call: Call, response: Response) {
            // 注意：此回调在子线程运行！
            val responseData = response.body.string()
            if (response.isSuccessful) {
                // 处理成功响应（需切回主线程更新UI）
                callback(responseData)
            } else {
                // 处理服务器错误（如 404）
                println("Request failed: ${response.code}")
            }
        }
    })
}