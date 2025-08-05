package space.think.cloud.cts.lib.form.validation

/**
 * ClassName: Validation
 * Description:
 * @date: 2022/10/21 17:50
 * @author: tanghy
 */
data class Validation(
    val requiredValidations:List<String>?,
    val expressionValidations:List<ExpressionValidation>
)