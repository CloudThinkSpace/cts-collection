package space.think.cloud.cts.lib.ui.form

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf

/**
 * ClassName: Item
 * Description:
 * @date: 2022/10/15 21:39
 * @author: tanghy
 */
data class Item(
    val name: String,
    val code: String,
    var isCheck: MutableState<Boolean> = mutableStateOf(false)
)
