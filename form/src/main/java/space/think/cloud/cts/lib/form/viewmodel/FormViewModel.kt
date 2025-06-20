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
            title = "1",
            value = "0",
            type = "TextType"
        ))
        _fields.add(FormField(
            id = "2",
            title = "1",
            value = "0",
            type = "UserType"
        ))
        _fields.add(FormField(
            id = "3",
            title = "1",
            value = "0",
            type = "IntegerType"
        ))
        _fields.add(FormField(
            id = "4",
            title = "1",
            value = "0",
            type = "NumberType"
        ))
        _fields.add(FormField(
            id = "5",
            title = "1",
            value = "0",
            type = "LongitudeType"
        ))
        _fields.add(FormField(
            id = "6",
            title = "1",
            value = "0",
            type = "AddressType"
        ))
        _fields.add(FormField(
            id = "7",
            title = "1",
            value = "0",
            type = "LatitudeType"
        ))
        _fields.add(FormField(
            id = "8",
            title = "1",
            value = "0",
            type = "PasswordType"
        ))
        _fields.add(FormField(
            id = "9",
            title = "1",
            value = "0",
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
            title = "1",
            value = "0",
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
            title = "1",
            value = "0",
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
            title = "1",
            value = "0",
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
            title = "1",
            value = "0",
            type = "SectionType"
        ))
        _fields.add(FormField(
            id = "14",
            title = "1",
            value = "0",
            type = "DateType"
        ))
        _fields.add(FormField(
            id = "15",
            title = "1",
            value = "0",
            type = "EmailType"
        ))
        _fields.add(FormField(
            id = "16",
            title = "拍照组件",
            value = "",
            type = "ImageType",
            subTitles = listOf("123","234")
        ))
    }

    fun updateField(updatedField: FormField) {
        val index = _fields.indexOfFirst { it.id == updatedField.id }
        if (index != -1) {
            _fields[index] = updatedField  // 只更新特定项
        }
    }

}