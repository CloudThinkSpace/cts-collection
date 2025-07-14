package space.think.cloud.cts.collection.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import space.think.cloud.cts.lib.network.RetrofitClient
import space.think.cloud.cts.lib.network.model.User
import space.think.cloud.cts.lib.network.model.request.RequestLogin
import space.think.cloud.cts.lib.network.model.response.ResponseAuth
import space.think.cloud.cts.lib.network.services.AuthService

class AuthViewModel : BaseViewModel() {

    private val _posts = MutableStateFlow<User?>(null)
    val data: StateFlow<User?> get() = _posts

    private val authService = RetrofitClient.createService<AuthService>()

    fun login(username: String, password: String, success: (ResponseAuth) -> Unit) = launch(
        {
            authService.login(RequestLogin(username, password))
        }
    ) {
        it?.let { auth ->
            _posts.value = auth.userInfo
            success(auth)
        }
    }

    /**
     * <h2>登录操作</h2>
     * <p>登出操作，清理缓存信息和内存中的token</p>
     */
    fun logout() = viewModelScope.launch {

    }

    override fun reset(){
        super.reset()
        _posts.value = null
    }

}
