package space.think.cloud.cts.lib.form.model

import space.think.cloud.cts.lib.ui.form.Item

/**
 * ClassName: Field
 * Description:
 * @date: 2022/10/14 12:11
 * @author: tanghy
 */
class Question {
    val name: String = ""
    val type: String = ""
    val title: String = ""
    var enabled: Boolean = true
    val required: Boolean = false
    val cached: Boolean = false
    val error: String? = null
    val unit: String? = null
    val description: String? = null
    val defaultValue: String? = null
    val lineMaxNum: Int? = null
    val items: List<Item>? = null
    val subTitles: List<String>? = null
    val placeholder: String? = null
}
