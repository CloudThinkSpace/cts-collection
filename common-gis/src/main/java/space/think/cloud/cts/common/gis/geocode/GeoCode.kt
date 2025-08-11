package space.think.cloud.cts.common.gis.geocode

import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

object GeoCode {

    // 单例 OkHttpClient（推荐）
    private val client by lazy {
        OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS).build()
    }

    /**
     * 发送 GET 请求（协程版）
     * @param timeoutMillis 超时时间（毫秒，默认 15 秒）
     */
    suspend fun get(
        lon: Double, lat: Double, token: String, timeoutMillis: Long = 15000
    ): String = withTimeout(timeoutMillis) {
        suspendCancellableCoroutine { continuation ->
            val url = "https://api.tianditu.gov.cn/geocoder?postStr=%7B%27lon%27%3A$lon,%27lat%27%3A$lat,%27ver%27%3A1%7D&type=geocode&tk=$token"
            val request = buildRequest(url)
            val call = client.newCall(request)

            // 设置协程取消时取消请求
            continuation.invokeOnCancellation {
                call.cancel()
            }

            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (continuation.isActive) {
                        continuation.resumeWithException(e)
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        if (!response.isSuccessful) {
                            Log.e("geocode error:","HTTP ${response.code} ${response.message}")
                        }

                        val body = response.body.string()
                        continuation.resume(body)
                    } catch (e: Exception) {
                        continuation.resumeWithException(e)
                    } finally {
                        response.close()
                    }
                }
            })
        }
    }

    private fun buildRequest(url: String, headers: Map<String, String>? = null): Request {
        val builder = Request.Builder().url(url)
        headers?.forEach { (key, value) ->
            builder.addHeader(key, value)
        }
        return builder.build()
    }
}