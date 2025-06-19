package space.think.cloud.cts.lib.network

object Constants {
    const val PAGE_SIZE: Int = 20
    object PostAPI {
        const val URL: String = "http://localhost:8080/"
        const val NAME = "post_api"
    }

    object SecondaryAPI {
        const val URL: String = "http://localhost:8080/"
        const val NAME = "another_api"
    }
}