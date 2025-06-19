package space.think.cloud.cts.collection.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

open class BaseViewModel: ViewModel() {

    protected val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> get() = _error

}