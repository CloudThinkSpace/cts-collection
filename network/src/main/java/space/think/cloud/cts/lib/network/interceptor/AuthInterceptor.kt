package space.think.cloud.cts.lib.network.interceptor

import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import space.think.cloud.cts.lib.network.TokenManager

class AuthInterceptor(
    private val tokenManager: TokenManager,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()

        // 使用 runBlocking 在拦截器中同步获取 Flow 数据
        val token = runBlocking {
            tokenManager.tokenFlow.first()
        }

        return if (token != null) {
            // 如果 token 存在，添加到请求头
            val newRequest = originalRequest.newBuilder().apply {
                if (token.isNotEmpty()) {
                    addHeader("Authorization", token)
                    addHeader("User-Agent", "Android")
                }
            }.build()
            chain.proceed(newRequest)
        } else {
            // 如果 token 不存在，直接继续请求
            chain.proceed(originalRequest)
        }
    }
}