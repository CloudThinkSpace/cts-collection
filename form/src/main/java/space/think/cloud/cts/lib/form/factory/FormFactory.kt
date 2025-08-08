package space.think.cloud.cts.lib.form.factory

import com.google.gson.Gson
import space.think.cloud.cts.lib.form.model.Form
import space.think.cloud.cts.lib.form.model.FormConfig


object FormFactory {

    fun createForm(content: String): Form {
        val gson = Gson()
        // 序列号成formConfig 对象
        val formConfig = gson.fromJson(content, FormConfig::class.java)
        val form = formConfig.form
        return form
    }
}