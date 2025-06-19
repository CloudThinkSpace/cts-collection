package space.think.cloud.cts.lib.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import space.think.cloud.cts.lib.network.services.ProjectService

object RetrofitClient {
    private const val BASE_URL = "https://jsonplaceholder.typicode.com/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val projectService: ProjectService by lazy {
        retrofit.create(ProjectService::class.java)
    }
}

