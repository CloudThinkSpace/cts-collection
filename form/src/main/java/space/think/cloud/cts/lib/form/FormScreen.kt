package space.think.cloud.cts.lib.form

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SaveAs
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import space.think.cloud.cts.common_datastore.DataStoreUtil
import space.think.cloud.cts.common_datastore.FormFieldDataStore
import space.think.cloud.cts.common_datastore.PreferencesKeys
import space.think.cloud.cts.lib.form.factory.WidgetFactory
import space.think.cloud.cts.lib.form.viewmodel.FormViewModel
import space.think.cloud.cts.lib.ui.project.ProjectData


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    modifier: Modifier = Modifier,
    project: ProjectData,
    code: String,
    formViewModel: FormViewModel = viewModel(),
    onBack: () -> Unit
) {

    var title by remember { mutableStateOf("") }
    val fields by remember { derivedStateOf { formViewModel.fields } }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {

        val dataStore = DataStoreUtil(context)
        val lon = dataStore.getData(PreferencesKeys.LON_KEY, 0.0).first()
        val lat = dataStore.getData(PreferencesKeys.LAT_KEY, 0.0).first()
        val fieldBuilder = FieldBuilder(FormFieldDataStore(context, project.id))
        fieldBuilder.withLon(lon)
        fieldBuilder.withLat(lat)

        // 查询表单模板
        formViewModel.getAllData(
            formTemplateId = project.formTemplateId,
            tableName = project.dataTableName,
            taskId = code,
        ) { formTemplate, task, formData ->
            // 设置名称
            title = formTemplate.title
            // 设置任务数据
            fieldBuilder.withTask(task)
            // 设置表单采集数据
            formData?.let {
                fieldBuilder.withData(formData)
            }
            scope.launch {
                val fields = fieldBuilder.createFields(formTemplate.content)
                formViewModel.updateFields(fields)
            }
        }
    }


    // 监听返回键
    BackHandler(enabled = true) {
        onBack()
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = {
                    Text(
                        title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onBack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            tint = Color.White,
                            contentDescription = "Localized description"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* do something */ }) {
                        Icon(
                            imageVector = Icons.Filled.SaveAs,
                            tint = Color.White,
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

                WidgetFactory.CreateWidget(field) { updated ->
                    formViewModel.updateField(updated)
                }
            }
        }
    }

}