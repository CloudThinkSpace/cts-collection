package space.think.cloud.cts.lib.form

import android.icu.text.DecimalFormat
import com.google.gson.Gson
import space.think.cloud.cts.lib.form.model.FormConfig
import space.think.cloud.cts.lib.form.model.Question

class FieldBuilder {

    private var lon = ""
    private var lat = ""
    private val df = DecimalFormat("#.######")

    fun withLon(lon: Double): FieldBuilder {
        this.lon = df.format(lon)
        return this
    }

    fun withLat(lat: Double): FieldBuilder {
        this.lat = df.format(lat)
        return this
    }


    fun createFields(content: String): List<FormField> {
        val gson = Gson()
        val formConfig = gson.fromJson(content, FormConfig::class.java)
        val form = formConfig.form
        val formFields = form.questions.mapIndexed { index, question ->

            val value = handlerLocation(question)

            FormField(
                id = index.toString(),
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

}