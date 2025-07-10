package space.think.cloud.cts.lib.form

import space.think.cloud.cts.lib.ui.form.MediaItem
import space.think.cloud.cts.lib.ui.form.Item
import space.think.cloud.cts.lib.ui.utils.StringUtil

data class FormField(

    val id: String,

    // 类型
    val type: String,

    // 值
    var value: String = "",

    // 标题
    val title: String,

    // 是否可用
    var enabled: Boolean = true,

    // 是否必填
    val required: Boolean = false,

    // 是否缓存
    val cached: Boolean = false,

    // 错误信息
    val error: String? = null,

    // 单位
    val unit: String? = null,

    // 描述
    val description: String? = null,

    // 默认值
    val defaultValue: String? = null,

    // 最大行数
    val lineMaxNum: Int? = null,

    // 多选或单选列表
    val items: List<Item>? = null,

    // 子名称列表（图片名称）
    val subTitles: List<String>? = null,

    // 默认显示值
    val placeholder: String = "",
) {
    fun getFieldValue(): String {
        return if (value.isEmpty() && defaultValue != null) {
            defaultValue
        } else {
            value
        }
    }

    fun getMediasToMap(): Map<Int, MediaItem> {
        return StringUtil.jsonToMap(getFieldValue())
    }
}