package space.think.cloud.cts.lib.network

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import space.think.cloud.cts.common_datastore.DataStoreUtil
import space.think.cloud.cts.lib.network.interceptor.AuthInterceptor
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val BASE_URL = "http://180.184.49.65/"

    private lateinit var retrofit: Retrofit
    private val appScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    fun initialize(context: Context) {

        val tokenManager = TokenManager(DataStoreUtil(context))

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager, appScope))
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    inline fun <reified T> createService(): T {
        return createService(T::class.java)
    }
}

