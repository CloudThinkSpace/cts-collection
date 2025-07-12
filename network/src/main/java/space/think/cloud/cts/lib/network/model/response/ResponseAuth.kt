package space.think.cloud.cts.lib.network.model.response

import space.think.cloud.cts.lib.network.model.User

data class ResponseAuth(
    val userInfo: User,
    val token: Token,
)

data class Token(
    val accessToken:String,
    val expire: Long
)
