package space.think.cloud.cts.lib.ui.task

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class TaskItem(
    val id: String,
    val status: Int,
    val name: String,
    val lon: String,
    val lat: String,
)

/**
 * ClassName: TaskContent
 * Description:
 * @date: 2022/11/1 17:40
 * @author: tanghy
 */
@Composable
fun TaskContent(
    modifier: Modifier = Modifier,
    taskList: List<TaskItem>,
    onClickDetail: ((TaskItem) -> Unit)? = null,
    onClick: (TaskItem) -> Unit
) {

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(10.dp)

    ) {

        itemsIndexed(
            items = taskList,
        ) { _, item ->

            TaskItem(
                leadText = if (item.status == 0) "未采" else "已采",
                leadBackgroundColor = if (item.status == 0) Color(0xFF17A211) else Color(0xfff55951),
                title = item.name,
                lon = item.lon,
                lat = item.lat,
                status = item.status,
                onClickDetail = {
                    onClickDetail?.invoke(item)
                }
            ) {
                onClick(item)
            }
        }
    }


}