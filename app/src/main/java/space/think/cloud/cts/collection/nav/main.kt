package space.think.cloud.cts.collection.nav

import space.think.cloud.cts.lib.ui.task.TaskItem

data object Login
data object Home
data object Help

data class TaskList(val projectId: String, val dataTableName: String)
data class TaskMapView(
    val projectId: String,
    val dataTableName: String,
    val taskItem: TaskItem?
)