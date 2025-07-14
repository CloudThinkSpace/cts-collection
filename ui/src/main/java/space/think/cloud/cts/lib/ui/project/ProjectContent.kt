package space.think.cloud.cts.lib.ui.project

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

data class ProjectData(
    val id: String,
    val title: String,
    val subTitle: String,
    val type: Int,
    val status: Int,
)

@Composable
fun ProjectContent(
    modifier: Modifier = Modifier,
    items: List<ProjectData>,
    onClick: (ProjectData) -> Unit
) {
    val context = LocalContext.current
    LazyColumn(
        modifier = modifier.padding(vertical = 10.dp)
    ) {
        items(
            items = items,
            itemContent = { item ->
                ProjectItem(
                    title = item.title,
                    subTitle = item.subTitle,
                    status = item.status,
                    type = item.status, onClick = {
                        if (item.status != 0) {
                            Toast.makeText(
                                context,
                                "项目[${item.title}]已经停止",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            onClick(item)
                        }
                    })
            }
        )
    }

}