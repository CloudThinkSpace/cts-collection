package space.think.cloud.cts.lib.ui.form

/**
 * ClassName: Item
 * Description:
 * @date: 2022/10/15 21:39
 * @author: tanghy
 */
data class Item(
    val name: String,
    val code: String,
    var isCheck: Boolean = false,
)

// 将选择的值数据转换成对应的名称
fun getItemNames(selectValue: String, items: List<Item>): String {

    val result = mutableListOf<String>()
    val selectValueList = selectValue.split(",")
    for (item in items) {
        val ok = selectValueList.contains(item.code)
        if (ok) {
            result.add(item.name)
        }
    }
    return result.joinToString(",")

}
