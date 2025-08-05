package space.think.cloud.cts.collection.nav

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import space.think.cloud.cts.lib.ui.project.ProjectData
import space.think.cloud.cts.lib.ui.task.TaskItem

@Serializable
data object Login : NavKey

@Serializable
data object Home : NavKey

@Serializable
data object Help : NavKey

@Serializable
data class Form(
    val code: String,
    @Contextual
    val project: ProjectData
) : NavKey

@Serializable
data class TaskList(
    @Contextual
    val project: ProjectData
) : NavKey

@Serializable
data class TaskMapView(
    @Contextual
    val project: ProjectData,
    @Contextual
    val taskItem: TaskItem?
) : NavKey