package space.think.cloud.cts.collection.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import space.think.cloud.cts.lib.network.model.response.Result

open class BaseViewModel : ViewModel() {

    protected val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

    protected fun <T> launch(
        block: suspend () -> Result<T>,
        errorFun: ((String) -> Unit)? = null,
        successFun: (T?) -> Unit
    ) =
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    block()
                }catch (e: Exception){
                    Result(500, e.toString(),null)
                }
            }
            if (result.code != 200) {
                result.msg.apply {
                    errorFun?.invoke(this)
                    _error.value = this
                }
            } else {
                successFun(result.data)
            }
        }

    open fun reset() {
        _error.value = null
    }

}