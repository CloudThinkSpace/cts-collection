package space.think.cloud.cts.lib.form

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import space.think.cloud.cts.lib.form.viewmodel.FormViewModel
import space.think.cloud.cts.lib.ui.form.widgets.CheckWidget
import space.think.cloud.cts.lib.ui.form.widgets.DateWidget
import space.think.cloud.cts.lib.ui.form.widgets.EmailWidget
import space.think.cloud.cts.lib.ui.form.widgets.ImageWidget
import space.think.cloud.cts.lib.ui.form.widgets.IntegerWidget
import space.think.cloud.cts.lib.ui.form.widgets.MoreChoiceWidget
import space.think.cloud.cts.lib.ui.form.widgets.NumberWidget
import space.think.cloud.cts.lib.ui.form.widgets.PasswordWidget
import space.think.cloud.cts.lib.ui.form.widgets.RadioWidget
import space.think.cloud.cts.lib.ui.form.widgets.SectionWidget
import space.think.cloud.cts.lib.ui.form.widgets.SingleChoiceWidget
import space.think.cloud.cts.lib.ui.form.widgets.TextWidget


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    modifier: Modifier = Modifier,
    title: String,
    viewModel: FormViewModel
) {

    val fields by remember { derivedStateOf { viewModel.fields } }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.Menu,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .padding(paddingValues)
                .fillMaxSize()
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

                    QuestionType.ImageType.type -> {
                        ImageWidget(
                            value = localField.value,
                            title = localField.title,
                            errorMsg = localField.error,
                            required = localField.required,
                            isError = localField.error?.isNotEmpty() == true,
                            enabled = localField.enabled,
                            subTitles = localField.subTitles ?: listOf(),
                            description = localField.description,
                        ) { newValue ->
                            val updated = localField.copy(value = newValue)
                            localField = updated
                            viewModel.updateField(updated)
                        }
                    }

                }
            }
        }
    }

}