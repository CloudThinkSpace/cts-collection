package space.think.cloud.cts.lib.network.model

/**
 * ClassName: User
 * Description:
 * @date: 2022/11/14 15:01
 * @author: tanghy
 */
data class User(
    val id: String,
    val uuid: String,
    val username: String,
    val nickname: String,
    val phone: String,
    val email: String
)