package space.think.cloud.cts.lib.form.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import space.think.cloud.cts.lib.form.FormField
import space.think.cloud.cts.lib.ui.form.Item

class FormViewModel:ViewModel() {

    private val _fields = mutableStateListOf<FormField>()
    val fields: List<FormField> = _fields

    init {
        _fields.add(FormField(
            id = "1",
            title = "文本组件",
            value = "",
            required = true,
            type = "TextType"
        ))
        _fields.add(FormField(
            id = "2",
            title = "用户组件",
            value = "",
            required = true,
            type = "UserType"
        ))
        _fields.add(FormField(
            id = "3",
            title = "整数组件",
            value = "",
            required = true,
            type = "IntegerType"
        ))
        _fields.add(FormField(
            id = "4",
            title = "数值组件",
            value = "",
            required = true,
            type = "NumberType"
        ))
        _fields.add(FormField(
            id = "5",
            title = "经度组件",
            value = "",
            required = true,
            type = "LongitudeType"
        ))
        _fields.add(FormField(
            id = "6",
            title = "地址组件",
            value = "",
            type = "AddressType"
        ))
        _fields.add(FormField(
            id = "7",
            title = "纬度组件",
            value = "",
            type = "LatitudeType"
        ))
        _fields.add(FormField(
            id = "8",
            title = "密码组件",
            value = "",
            type = "PasswordType"
        ))
        _fields.add(FormField(
            id = "9",
            title = "单选组件",
            value = "",
            type = "SingleChoiceType",
            items = listOf(
                Item(
                    name = "111",
                    code = "1"
                ),
                Item(
                    name = "111",
                    code = "2"
                ),
                Item(
                    name = "111",
                    code = "3"
                )
            )
        ))
        _fields.add(FormField(
            id = "10",
            title = "多选组件",
            value = "",
            type = "MoreChoiceType",
            items = listOf(
                Item(
                    name = "111",
                    code = "1"
                ),
                Item(
                    name = "111",
                    code = "2"
                ),
                Item(
                    name = "111",
                    code = "3"
                )
            )
        ))
        _fields.add(FormField(
            id = "11",
            title = "列表多选组件",
            value = "",
            type = "CheckType",
            items = listOf(
                Item(
                    name = "111",
                    code = "1"
                ),
                Item(
                    name = "111",
                    code = "2"
                ),
                Item(
                    name = "111",
                    code = "3"
                )
            )
        ))
        _fields.add(FormField(
            id = "12",
            title = "列表单选组件",
            value = "",
            type = "RadioType",
            items = listOf(
                Item(
                    name = "111",
                    code = "1"
                ),
                Item(
                    name = "111",
                    code = "2"
                ),
                Item(
                    name = "111",
                    code = "3"
                )
            )
        ))
        _fields.add(FormField(
            id = "13",
            title = "分割组件",
            value = "",
            type = "SectionType"
        ))
        _fields.add(FormField(
            id = "14",
            title = "日期组件",
            value = "",
            type = "DateType"
        ))
        _fields.add(FormField(
            id = "15",
            title = "邮箱组件",
            value = "",
            type = "EmailType"
        ))
        _fields.add(FormField(
            id = "16",
            title = "拍照组件",
            value = "",
            type = "ImageType",
            subTitles = listOf("123","234")
        ))
        _fields.add(FormField(
            id = "17",
            title = "视频组件",
            value = "",
            type = "VideoType",
            required = true,
            subTitles = listOf("123","234")
        ))
    }

    /**
     * 更新字段信息
     */
    fun updateField(updatedField: FormField) {
        // 根据id获取数组位置
        val index = _fields.indexOfFirst { it.id == updatedField.id }
        if (index != -1) {
            // 更新字段
            _fields[index] = updatedField  // 只更新特定项
        }
    }

}