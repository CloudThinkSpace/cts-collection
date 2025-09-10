package space.think.cloud.cts.lib.form

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.SaveAs
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import space.think.cloud.cts.common_datastore.DataStoreUtil
import space.think.cloud.cts.common_datastore.FormFieldDataStore
import space.think.cloud.cts.common_datastore.PreferencesKeys
import space.think.cloud.cts.lib.form.factory.FormFactory
import space.think.cloud.cts.lib.form.factory.WidgetFactory
import space.think.cloud.cts.lib.form.validation.ExpressionValidation
import space.think.cloud.cts.lib.form.validation.ValidationManager
import space.think.cloud.cts.lib.form.viewmodel.FormViewModel
import space.think.cloud.cts.lib.ui.project.ProjectData


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormScreen(
    modifier: Modifier = Modifier,
    project: ProjectData,
    taskId: String,
    formViewModel: FormViewModel = viewModel(),
    onBack: () -> Unit
) {

    // 表单标题
    var title by remember { mutableStateOf("") }
    // 表单字段列表
    val fields by remember { derivedStateOf { formViewModel.fields } }
    // 错误信息
    val errorMsg by formViewModel.error.collectAsState()
    // 是否提交
    var isSubmit by remember { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // 表单表达式列表
    val validationExpressions = remember { mutableListOf<ExpressionValidation>() }
    // 展示数据对象
    val snackbarHostState = remember { SnackbarHostState() }
    // 移动位置
    val lazyListState = rememberLazyListState()
    // 键盘控制器
    val localSoftwareKeyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {

        val dataStore = DataStoreUtil(context)
        // 读取dataStore中的数据
        val lon = dataStore.getData(PreferencesKeys.LON_KEY, 0.0).first()
        val lat = dataStore.getData(PreferencesKeys.LAT_KEY, 0.0).first()
        val fieldBuilder = FieldBuilder(FormFieldDataStore(context, project.id))
        // 设置经纬度
        fieldBuilder.withLon(lon)
        fieldBuilder.withLat(lat)

        // 查询表单模板
        formViewModel.getAllData(
            formTemplateId = project.formTemplateId,
            tableName = project.dataTableName,
            taskId = taskId,
        ) { formTemplate, task, formData ->

            // 创建form对象
            val form = FormFactory.createForm(formTemplate.content)
            // 设置名称
            title = formTemplate.title
            // 判断表达式是否存在，存在添加到表达式列表中
            form.validations?.let {
                validationExpressions.apply {
                    clear()
                    addAll(it.expressionValidations)
                }
            }
            formViewModel.setTaskData(task)
            // 设置任务数据
            fieldBuilder.withTask(task)
            // 设置表单采集数据
            if (formData != null) {
                fieldBuilder.withData(formData)
                formViewModel.isUpdate = true
            } else {
                formViewModel.isUpdate = false
            }
            // 更新表单
            scope.launch {
                val fields = fieldBuilder.createFields(form)
                formViewModel.updateFields(fields)
            }
        }
    }

    LaunchedEffect(errorMsg) {
        errorMsg?.let {
            snackbarHostState.showSnackbar(it)
        }
        isSubmit = false
    }


    // 监听返回键
    BackHandler(enabled = true) {
        formViewModel.reset()
        onBack()
    }

    Box {

        Scaffold(modifier = modifier, snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }, topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                title = {
                    Text(
                        title, maxLines = 1, overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        formViewModel.reset()
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
                    IconButton(onClick = {
                        localSoftwareKeyboardController?.hide()
                        // 校验管理对象
                        val validationManager = ValidationManager()
                        // 添加校验表达式
                        validationManager.addAll(validationExpressions)
                        // 校验数据
                        validationManager.validate(
                            formViewModel.fields,
                            onPass = {
                                isSubmit = true
                                scope.launch {
                                    // 保存缓存数据
                                    formViewModel.saveToDataStore(
                                        FormFieldDataStore(
                                            context,
                                            project.id
                                        )
                                    )
                                    // 提交表单数据
                                    formViewModel.submitData(
                                        context = context,
                                        taskId = taskId,
                                        project = project
                                    ) {
                                        isSubmit = false
                                        Toast.makeText(context, "提交数据成功", Toast.LENGTH_SHORT)
                                            .show()
                                        scope.launch {
                                            formViewModel.reset()
                                        }
                                        onBack()
                                    }
                                }
                            },
                            onFail = { formField, msg, index ->
                                scope.launch {
                                    // 更新表单信息
                                    formField?.let {
                                        formViewModel.updateField(formField)
                                    }
                                    // 判断是否需要移动到指定的组件上
                                    lazyListState.animateScrollToItem(index)
                                    // 显示错误信息
                                    snackbarHostState.showSnackbar(msg)
                                }
                            }
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Filled.SaveAs,
                            tint = Color.White,
                            contentDescription = "Localized description"
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        }) { paddingValues ->
            LazyColumn(
                modifier = modifier
                    .padding(paddingValues)
                    .fillMaxSize(), state = lazyListState
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

        if (isSubmit) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = Color(0x617E7E7E))
                    .pointerInteropFilter {
                        true
                    }, contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }


    }


}