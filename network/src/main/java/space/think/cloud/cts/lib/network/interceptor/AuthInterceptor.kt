package space.think.cloud.cts.lib.network.interceptor

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import space.think.cloud.cts.lib.network.TokenManager

class AuthInterceptor(
    private val tokenManager: TokenManager,
    private val coroutineScope: CoroutineScope
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val deferredToken = CompletableDeferred<String>()

        coroutineScope.launch(Dispatchers.IO) {
            try {
                deferredToken.complete(tokenManager.getToken())
            } catch (e: Exception) {
                deferredToken.complete("")
            }
        }

        return runBlocking {
            val token = deferredToken.await()
            val request = chain.request().newBuilder().apply {
                if (token.isNotEmpty()) {
                    addHeader("Authorization", token)
                    addHeader("User-Agent", "Android")
                }
            }.build()
            chain.proceed(request)
        }
    }
}