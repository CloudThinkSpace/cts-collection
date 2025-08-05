package space.think.cloud.cts.lib.form.model

import space.think.cloud.cts.lib.form.validation.Validation

/**
 * ClassName: Form
 * Description:
 * @date: 2022/10/14 14:45
 * @author: tanghy
 * @param name 表单名称
 * @param title 表单标题
 * @param description 表单描述
 * @param version 表单版本
 * @param questions 表单问题列表
 * @param validations 表单验证
 * @param auto 字段赋值，元数据数据与表单问题对应关系
 */
data class Form(
    val name: String,
    val title: String,
    val description: String,
    val version:String,
    val questions: List<Question>,
    val validations: Validation?,
    val auto:Map<String,String>?
)