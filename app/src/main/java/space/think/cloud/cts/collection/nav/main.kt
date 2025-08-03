package space.think.cloud.cts.collection.nav

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import space.think.cloud.cts.lib.ui.task.TaskItem

@Serializable
data object Login : NavKey
@Serializable
data object Home : NavKey
@Serializable
data object Help : NavKey
@Serializable
data class Form(val code: String) : NavKey

@Serializable
data class TaskList(val projectId: String, val dataTableName: String) : NavKey
@Serializable
data class TaskMapView(
    val projectId: String,
    val dataTableName: String,
    @Contextual
    val taskItem: TaskItem?
) : NavKey