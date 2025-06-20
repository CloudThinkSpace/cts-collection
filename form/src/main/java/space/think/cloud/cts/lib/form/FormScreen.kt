package space.think.cloud.cts.lib.form

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import space.think.cloud.cts.lib.form.viewmodel.FormViewModel
import space.think.cloud.cts.lib.ui.form.widgets.CheckWidget
import space.think.cloud.cts.lib.ui.form.widgets.DateWidget
import space.think.cloud.cts.lib.ui.form.widgets.EmailWidget
import space.think.cloud.cts.lib.ui.form.widgets.IntegerWidget
import space.think.cloud.cts.lib.ui.form.widgets.MoreChoiceWidget
import space.think.cloud.cts.lib.ui.form.widgets.NumberWidget
import space.think.cloud.cts.lib.ui.form.widgets.PasswordWidget
import space.think.cloud.cts.lib.ui.form.widgets.RadioWidget
import space.think.cloud.cts.lib.ui.form.widgets.SectionWidget
import space.think.cloud.cts.lib.ui.form.widgets.SingleChoiceWidget
import space.think.cloud.cts.lib.ui.form.widgets.TextWidget


@Composable
fun FormScreen(
    modifier: Modifier = Modifier,
    viewModel: FormViewModel
) {

    val fields by remember { derivedStateOf { viewModel.fields } }

    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        items(
            items = fields,
            key = { it.id }
        ) { field ->
            // 使用独立的状态管理
            var localField by remember(field.id) { mutableStateOf(field) }

            LaunchedEffect(field) {
                localField = field // 当外部field变化时更新本地状态
            }

            when (localField.type) {
                QuestionType.TextType.type -> {
                    TextWidget(
                        value = localField.value,
                        title = localField.title,
                        unit = localField.unit,
                        errorMsg = localField.error,
                        required = localField.required,
                        isError = localField.error?.isNotEmpty() == true,
                        enabled = localField.enabled,
                        description = localField.description
                    ) { newValue ->
                        val updated = localField.copy(value = newValue)
                        localField = updated
                        viewModel.updateField(updated)
                    }
                }

                QuestionType.UserType.type -> {
                    TextWidget(
                        value = localField.value,
                        title = localField.title,
                        unit = localField.unit,
                        errorMsg = localField.error,
                        required = localField.required,
                        isError = localField.error?.isNotEmpty() == true,
                        enabled = localField.enabled,
                        description = localField.description
                    ) { newValue ->
                        val updated = localField.copy(value = newValue)
                        localField = updated
                        viewModel.updateField(updated)
                    }
                }

                QuestionType.IntegerType.type -> {
                    IntegerWidget(
                        value = localField.value,
                        title = localField.title,
                        unit = localField.unit,
                        errorMsg = localField.error,
                        required = localField.required,
                        isError = localField.error?.isNotEmpty() == true,
                        enabled = localField.enabled,
                        description = localField.description
                    ) { newValue ->
                        val updated = localField.copy(value = newValue)
                        localField = updated
                        viewModel.updateField(updated)
                    }
                }

                QuestionType.NumberType.type -> {
                    NumberWidget(
                        value = localField.value,
                        title = localField.title,
                        unit = localField.unit,
                        errorMsg = localField.error,
                        required = localField.required,
                        isError = localField.error?.isNotEmpty() == true,
                        enabled = localField.enabled,
                        description = localField.description
                    ) { newValue ->
                        val updated = localField.copy(value = newValue)
                        localField = updated
                        viewModel.updateField(updated)
                    }
                }

                QuestionType.LongitudeType.type -> {
                    NumberWidget(
                        value = localField.value,
                        title = localField.title,
                        unit = localField.unit,
                        errorMsg = localField.error,
                        required = localField.required,
                        isError = localField.error?.isNotEmpty() == true,
                        enabled = localField.enabled,
                        description = localField.description
                    ) { newValue ->
                        val updated = localField.copy(value = newValue)
                        localField = updated
                        viewModel.updateField(updated)
                    }
                }

                QuestionType.LatitudeType.type -> {
                    NumberWidget(
                        value = localField.value,
                        title = localField.title,
                        unit = localField.unit,
                        errorMsg = localField.error,
                        required = localField.required,
                        isError = localField.error?.isNotEmpty() == true,
                        enabled = localField.enabled,
                        description = localField.description
                    ) { newValue ->
                        val updated = localField.copy(value = newValue)
                        localField = updated
                        viewModel.updateField(updated)
                    }
                }

                QuestionType.AddressType.type -> {
                    TextWidget(
                        value = localField.value,
                        title = localField.title,
                        unit = localField.unit,
                        errorMsg = localField.error,
                        required = localField.required,
                        isError = localField.error?.isNotEmpty() == true,
                        enabled = localField.enabled,
                        description = localField.description
                    ) { newValue ->
                        val updated = localField.copy(value = newValue)
                        localField = updated
                        viewModel.updateField(updated)
                    }
                }

                QuestionType.EmailType.type -> {
                    EmailWidget(
                        value = localField.value,
                        title = localField.title,
                        unit = localField.unit,
                        errorMsg = localField.error,
                        required = localField.required,
                        isError = localField.error?.isNotEmpty() == true,
                        enabled = localField.enabled,
                        description = localField.description
                    ) { newValue ->
                        val updated = localField.copy(value = newValue)
                        localField = updated
                        viewModel.updateField(updated)
                    }
                }

                QuestionType.PasswordType.type -> {
                    PasswordWidget(
                        value = localField.value,
                        title = localField.title,
                        unit = localField.unit,
                        errorMsg = localField.error,
                        required = localField.required,
                        isError = localField.error?.isNotEmpty() == true,
                        enabled = localField.enabled,
                        description = localField.description
                    ) { newValue ->
                        val updated = localField.copy(value = newValue)
                        localField = updated
                        viewModel.updateField(updated)
                    }
                }

                QuestionType.SectionType.type -> {
                    SectionWidget(title = field.title)
                }

                QuestionType.SingleChoiceType.type -> {
                    SingleChoiceWidget(
                        value = localField.value,
                        title = localField.title,
                        unit = localField.unit,
                        errorMsg = localField.error,
                        required = localField.required,
                        isError = localField.error?.isNotEmpty() == true,
                        enabled = localField.enabled,
                        description = localField.description,
                        items = localField.items ?: listOf(),
                    ) { newValue ->
                        val updated = localField.copy(
                            value = newValue.code,
                            items = field.items
                        )
                        localField = updated
                        viewModel.updateField(updated)
                    }
                }

                QuestionType.MoreChoiceType.type -> {
                    MoreChoiceWidget(
                        value = localField.value,
                        title = localField.title,
                        unit = localField.unit,
                        errorMsg = localField.error,
                        required = localField.required,
                        isError = localField.error?.isNotEmpty() == true,
                        enabled = localField.enabled,
                        description = localField.description,
                        items = localField.items ?: listOf(),
                    ) { newValue ->

                        val updated = localField.copy(
                            value = newValue.joinToString(",") { checkItem ->
                                checkItem.code
                            },
                            items = field.items
                        )
                        localField = updated
                        viewModel.updateField(updated)
                    }
                }

                QuestionType.CheckType.type -> {
                    CheckWidget(
                        value = localField.value,
                        title = localField.title,
                        errorMsg = localField.error,
                        required = localField.required,
                        isError = localField.error?.isNotEmpty() == true,
                        enabled = localField.enabled,
                        description = localField.description,
                        items = localField.items ?: listOf(),
                    ) { newValue ->
                        val updated = localField.copy(
                            value = newValue.joinToString(",") { checkItem ->
                                checkItem.code
                            },
                            items = field.items
                        )
                        localField = updated
                        viewModel.updateField(updated)
                    }
                }

                QuestionType.RadioType.type -> {
                    RadioWidget(
                        value = localField.value,
                        title = localField.title,
                        errorMsg = localField.error,
                        required = localField.required,
                        isError = localField.error?.isNotEmpty() == true,
                        enabled = localField.enabled,
                        description = localField.description,
                        items = localField.items ?: listOf(),
                    ) { newValue ->
                        val updated = localField.copy(
                            value = newValue.code,
                            items = field.items
                        )
                        localField = updated
                        viewModel.updateField(updated)
                    }
                }

                QuestionType.DateType.type -> {
                    DateWidget(
                        value = localField.value,
                        title = localField.title,
                        unit = localField.unit,
                        errorMsg = localField.error,
                        required = localField.required,
                        isError = localField.error?.isNotEmpty() == true,
                        enabled = localField.enabled,
                        description = localField.description,
                    ) { newValue ->
                        val updated = localField.copy(value = newValue)
                        localField = updated
                        viewModel.updateField(updated)
                    }
                }
//                QuestionType.ImageType.type -> {
//                    ImageWidget(
//                        value = localField.value,
//                        title = localField.title,
//                        errorMsg = localField.error,
//                        required = localField.required,
//                        isError = localField.error?.isNotEmpty() == true,
//                        enabled = localField.enabled,
//                        description = localField.description,
//                    ) { newValue ->
//                        val updated = localField.copy(value = newValue)
//                        localField = updated
//                        viewModel.updateField(updated)
//                    }
//                }

            }
        }
    }
}