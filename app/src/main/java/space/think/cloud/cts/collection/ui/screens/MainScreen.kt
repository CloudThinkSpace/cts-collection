package space.think.cloud.cts.collection.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import space.think.cloud.cts.collection.nav.Form
import space.think.cloud.cts.collection.nav.Help
import space.think.cloud.cts.collection.nav.Home
import space.think.cloud.cts.collection.nav.Login
import space.think.cloud.cts.collection.nav.Project
import space.think.cloud.cts.collection.nav.TaskList
import space.think.cloud.cts.collection.nav.TaskMapView
import space.think.cloud.cts.collection.nav.TopLevelBackStack
import space.think.cloud.cts.lib.form.FormScreen

/**
 * 该界面是主界面，
 */
@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val topLevelBackStack = remember { TopLevelBackStack<Any>(Project) }
    val backStack = rememberNavBackStack(Home)
    var selectTaskId by remember { mutableStateOf("") }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeAt(backStack.lastIndex) },
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<Home> {
                HomeScreen(
                    backStack = backStack,
                    topLevelBackStack = topLevelBackStack,
                )
            }
            entry<Login> {
                LoginScreen {
                    backStack.add(Home)
                }
            }
            entry<Help> {
                HelpScreen(modifier = modifier)
            }
            entry<TaskMapView> {

                TaskMapViewScreen(
                    project = it.project,
                    selectTaskId = selectTaskId,
                    onSelectTask = {
                        selectTaskId = it
                    },
                    onBack = {
                        backStack.removeAt(backStack.lastIndex)
                    }
                ) { taskCode ->
                    // 导航到表单页面，传入任务编号
                    backStack.add(
                        Form(
                            code = taskCode,
                            project = it.project
                        )
                    )
                }
            }
            entry<TaskList> {
                TaskScreen(
                    project = it.project,
                    onBack = {
                        backStack.removeAt(backStack.lastIndex)
                    }
                ) { taskItem ->

                    selectTaskId = taskItem?.id ?: ""

                    backStack.add(
                        TaskMapView(
                            project = it.project,
                            taskItem
                        )
                    )
                }
            }
            entry<Form> {
                FormScreen(
                    modifier = modifier,
                    project = it.project,
                    code = it.code
                ) {
                    backStack.removeAt(backStack.lastIndex)
                }
            }
        }

    )


}

