package space.think.cloud.cts.lib.form.factory

import android.content.Intent
import android.icu.text.DecimalFormat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import kotlinx.coroutines.flow.first
import space.think.cloud.cts.common_datastore.DataStoreUtil
import space.think.cloud.cts.common_datastore.PreferencesKeys
import space.think.cloud.cts.lib.form.FormField
import space.think.cloud.cts.lib.form.ImagePreviewActivity
import space.think.cloud.cts.lib.form.QuestionType
import space.think.cloud.cts.lib.ui.form.MediaItem
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
import space.think.cloud.cts.lib.ui.form.widgets.VideoWidget
import space.think.cloud.cts.lib.ui.utils.DateUtil
import space.think.cloud.cts.lib.ui.utils.ImageUtil
import space.think.cloud.cts.lib.ui.utils.StringUtil
import java.util.Date

object WidgetFactory {

    @Composable
    fun CreateWidget(field: FormField, onChange: (FormField) -> Unit) {

        val context = LocalContext.current
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
                    val updated = localField.copy(value = newValue, error = null)
                    localField = updated
                    onChange(updated)
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
                    val updated = localField.copy(value = newValue, error = null)
                    localField = updated
                    onChange(updated)
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
                    val updated = localField.copy(value = newValue, error = null)
                    localField = updated
                    onChange(updated)
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
                    val updated = localField.copy(value = newValue, error = null)
                    localField = updated
                    onChange(updated)
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
                    val updated = localField.copy(value = newValue, error = null)
                    localField = updated
                    onChange(updated)
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
                    val updated = localField.copy(value = newValue, error = null)
                    localField = updated
                    onChange(updated)
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
                    val updated = localField.copy(value = newValue, error = null)
                    localField = updated
                    onChange(updated)
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
                    val updated = localField.copy(value = newValue, error = null)
                    localField = updated
                    onChange(updated)
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
                    val updated = localField.copy(value = newValue, error = null)
                    localField = updated
                    onChange(updated)
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
                        items = field.items,
                        error = null
                    )
                    localField = updated
                    onChange(updated)
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
                        items = field.items,
                        error = null
                    )
                    localField = updated
                    onChange(updated)
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
                        items = field.items,
                        error = null
                    )
                    localField = updated
                    onChange(updated)
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
                        items = field.items,
                        error = null
                    )
                    localField = updated
                    onChange(updated)
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
                    val updated = localField.copy(value = newValue, error = null)
                    localField = updated
                    onChange(updated)
                }
            }

            QuestionType.ImageType.type -> {
                ImageWidget(
                    value = localField.getMediasToMap(),
                    title = localField.title,
                    errorMsg = localField.error,
                    required = localField.required,
                    isError = localField.error?.isNotEmpty() == true,
                    enabled = localField.enabled,
                    subTitles = localField.subTitles ?: listOf(),
                    description = localField.description,
                    onPreview = {
                        val intent =
                            Intent(context, ImagePreviewActivity::class.java).apply {
                                putStringArrayListExtra(
                                    "uris",
                                    ArrayList(listOf<String>(it.toString()))
                                )
                            }
                        context.startActivity(intent)
                    },
                    onDelete = {
                        // 更新图片
                        val temp = localField.getMediasToMap().toMutableMap()
                        temp.remove(it)
                        val updated =
                            localField.copy(value = StringUtil.mapToString(temp), error = null)
                        localField = updated
                        onChange(updated)
                    }
                ) { path, selectIndex ->

                    val dataStoreUtil = DataStoreUtil(context)
                    // 获取经纬度数据
                    val lon = dataStoreUtil.getData(PreferencesKeys.LON_KEY, 0.0).first()
                    val lat = dataStoreUtil.getData(PreferencesKeys.LAT_KEY, 0.0).first()
                    val address = dataStoreUtil.getData(PreferencesKeys.ADDRESS_KEY, "").first()
                    val df = DecimalFormat("#.######")
                    // 准备表格数据
                    val tableData = listOf(
                        listOf("经度:", df.format(lon)),
                        listOf("纬度:", df.format(lat)),
                        listOf("地址:", address),
                        listOf(
                            "时间:",
                            DateUtil.dateToString(Date(), "yyyy年MM月dd日 HH:mm:ss")
                        )
                    )
                    // 加水印并保存图片
                    ImageUtil.printWatermark(
                        context,
                        tableData = tableData,
                        uri = path.toUri()
                    ) {
                        // 更新图片
                        val temp = localField.getMediasToMap().toMutableMap()
                        temp[selectIndex] =
                            MediaItem(
                                name = localField.subTitles!![selectIndex],
                                path = it.toString()
                            )
                        val updated =
                            localField.copy(value = StringUtil.mapToString(temp), error = null)
                        localField = updated
                        onChange(updated)
                    }
                }
            }

            QuestionType.VideoType.type -> {
                VideoWidget(
                    value = localField.getMediasToMap(),
                    title = localField.title,
                    errorMsg = localField.error,
                    required = localField.required,
                    isError = localField.error?.isNotEmpty() == true,
                    enabled = localField.enabled,
                    subTitles = localField.subTitles ?: listOf(),
                    description = localField.description,
                    onDelete = {
                        // 更新图片
                        val temp = localField.getMediasToMap().toMutableMap()
                        temp.remove(it)
                        val updated =
                            localField.copy(value = StringUtil.mapToString(temp), error = null)
                        localField = updated
                        onChange(updated)
                    }
                ) { path, selectIndex ->
                    // 更新视频
                    val temp = localField.getMediasToMap().toMutableMap()
                    temp[selectIndex] =
                        MediaItem(
                            name = localField.subTitles!![selectIndex],
                            path = path
                        )
                    val updated =
                        localField.copy(value = StringUtil.mapToString(temp), error = null)
                    localField = updated
                    onChange(updated)
                }
            }

        }
    }
}