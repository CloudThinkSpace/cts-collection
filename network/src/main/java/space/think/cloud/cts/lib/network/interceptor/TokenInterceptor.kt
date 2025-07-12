package space.think.cloud.cts.lib.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

/**
 * ClassName: TokenInterceptor
 * Description:
 * @date: 2022/12/22 19:51
 * @author: tanghy
 */
class TokenInterceptor():Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request().newBuilder().also {
                it.addHeader("Authorization", "")
                it.addHeader("User-Agent", "Android")
            }.build()
        )
    }
}