package space.think.cloud.cts.lib.ui.form

/**
 * ClassName: ImageItem
 * Description:
 * @date: 2022/10/16 15:57
 * @author: tanghy
 */
data class ImageItem(
    val name: String,
    var loading: Boolean = false,
    var path: String? = null,
)
