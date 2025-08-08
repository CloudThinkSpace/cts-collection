package space.think.cloud.cts.lib.form.validation

import space.think.cloud.cts.lib.form.FormField
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

class ValidationManager {
    // 表达式列表
    private val expressions = mutableListOf<ExpressionValidation>()
    private val scriptEngine: ScriptEngine = ScriptEngineManager().getEngineByName("javascript")

    fun validate(
        formFields: List<FormField>,
        onPass: () -> Unit,
        onFail: (FormField?, String, Int) -> Unit
    ) {

        // 遍历数据是否必填验证
        for (indexedValue in formFields.withIndex()) {
            val formField = indexedValue.value
            val index = indexedValue.index
            // 判断是否必填
            val pass = validateRequired(formField)
            // 判断必填验证是否通过
            if (!pass) {
                onFail(
                    formField.copy(error = "【${formField.title}】为必填字段，不能为空！"),
                    "【${formField.title}】为必填字段，不能为空！",
                    index
                )
                return
            }

        }
        // 判断表达式是否通过
        val expression = validateExpressions(formFields)
        if (!expression.first.isNullOrEmpty()) {
            expression.first?.let {
                onFail(null, it, -1)
                return
            }
        }
        // 验证通过
        onPass()
    }

    fun addAll(expressions: List<ExpressionValidation>) {
        this.expressions.addAll(expressions)
    }

    /**
     * 判断是否必填，如果值为空，不通过，返回false，其他返回true
     */
    private fun validateRequired(formField: FormField): Boolean {
        return !(formField.required && formField.value.isEmpty())
    }

    /**
     * 判断表达式是否通过
     */
    private fun validateExpressions(formFields: List<FormField>): Pair<String?, Int> {

        formFields.forEach {
            scriptEngine.put(it.name, it.value)
        }

        for (expression in expressions) {

            val eval = scriptEngine.eval(expression.expression)
            if (eval is Boolean && eval == false) {
                return Pair(expression.message, -1)
            }
        }

        return Pair(null, -1)
    }
}