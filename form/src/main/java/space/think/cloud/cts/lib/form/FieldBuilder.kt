package space.think.cloud.cts.lib.form

import android.icu.text.DecimalFormat
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import space.think.cloud.cts.common_datastore.FormFieldDataStore
import space.think.cloud.cts.lib.form.model.FormConfig
import space.think.cloud.cts.lib.form.model.Question

class FieldBuilder(private val dataStore: FormFieldDataStore) {

    private var lon = ""
    private var lat = ""
    private val df = DecimalFormat("#.######")

    // 已采的数据
    private val dataMap = mutableMapOf<String, Any>()

    // 任务数据
    private val taskMap = mutableMapOf<String, Any>()

    /**
     * 设置经度
     */
    fun withLon(lon: Double): FieldBuilder {
        this.lon = df.format(lon)
        return this
    }

    /**
     * 设置纬度
     */
    fun withLat(lat: Double): FieldBuilder {
        this.lat = df.format(lat)
        return this
    }

    /**
     * 设置数据
     */
    fun withData(data: Map<String, Any>): FieldBuilder {
        data.forEach {
            dataMap.put(it.key, it.value)
        }
        return this
    }

    /**
     * 设置任务数据
     */
    fun withTask(data: Map<String, Any>): FieldBuilder {
        data.forEach {
            taskMap.put(it.key, it.value)
        }
        return this
    }


    suspend fun createFields(content: String): List<FormField> {
        val gson = Gson()
        // 序列号成formConfig 对象
        val formConfig = gson.fromJson(content, FormConfig::class.java)
        val form = formConfig.form
        // 值从任务数据中匹配的
        val autoMap = form.auto
        // 将question 转换成formField对象
        val formFields = form.questions.mapIndexed { index, question ->

            val value = handlerValue(question, autoMap)

            FormField(
                id = index.toString(),
                name = question.name,
                value = value,
                type = question.type,
                title = question.title,
                required = question.required,
                subTitles = question.subTitles,
                enabled = question.enabled,
                cached = question.cached,
                error = question.error,
                unit = question.unit,
                description = question.description,
                defaultValue = question.defaultValue,
                lineMaxNum = question.lineMaxNum,
                placeholder = question.placeholder ?: "",
                items = question.items
            )
        }

        return formFields
    }

    /**
     * 处理坐标
     */
    private fun handlerLocation(question: Question): String {
        return if (question.type == QuestionType.LongitudeType.type) {
            lon
        } else if (question.type == QuestionType.LatitudeType.type) {
            lat
        } else {
            ""
        }
    }

    /**
     * 处理已采数据
     */
    private fun handlerData(question: Question): String? {
        val value = dataMap[question.name]
        return value?.toString() ?: ""
    }

    /**
     * 处理其他字段值
     */
    private fun handlerTask(question: Question, autoMap: Map<String, String>): String? {
        val taskName = autoMap[question.name]
        return if (taskName.isNullOrEmpty()) {
            null
        } else {
            taskMap[taskName]?.toString()
        }
    }

    /**
     * 从缓存中获取数据
     */
    private suspend fun handlerCache(question: Question): String? {
        return dataStore.get(question.name, "").first()
    }

    /**
     * 处理所有数据
     */
    private suspend fun handlerValue(question: Question, autoMap: Map<String, String>?): String {
        // 处理已采数据
        val dataValue = handlerData(question)
        // 处理当前经纬度
        val locationValue = handlerLocation(question)
        // 任务数据
        val taskValue = if (autoMap != null) handlerTask(question, autoMap) else ""
        // 获取缓存数据
        val cacheValue = handlerCache(question)
        val value = dataValue ?: taskValue.coalesceWith(locationValue).coalesceWith(cacheValue)
        return value ?: ""
    }

    // 扩展函数形式（可选）
    fun String?.coalesceWith(other: String?): String? = if (this.isNullOrEmpty()) other else this


}