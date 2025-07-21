package space.think.cloud.cts.collection.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import space.think.cloud.cts.collection.nav.Help
import space.think.cloud.cts.collection.nav.Home
import space.think.cloud.cts.collection.nav.Login
import space.think.cloud.cts.collection.nav.Project
import space.think.cloud.cts.collection.nav.TaskList
import space.think.cloud.cts.collection.nav.TaskMapView
import space.think.cloud.cts.collection.nav.TopLevelBackStack

/**
 * 该界面是主界面，
 */
@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val mainLevelRouteStack = remember { TopLevelBackStack<Any>(Home) }
    val topLevelBackStack = remember { TopLevelBackStack<Any>(Project) }
    NavDisplay(
        backStack = mainLevelRouteStack.backStack,
        onBack = { mainLevelRouteStack.removeLast() },
        entryProvider = entryProvider {
            entry<Home> {
                HomeScreen(
                    mainLevelRouteStack = mainLevelRouteStack,
                    topLevelBackStack = topLevelBackStack,
                )
            }
            entry<Login> {
                LoginScreen {
                    mainLevelRouteStack.addTopLevel(Home)
                }
            }
            entry<Help> {
                HelpScreen(modifier = modifier)
            }
            entry<TaskMapView> {
                TaskMapViewScreen(
                    projectId = it.projectId,
                    dataTableName = it.dataTableName,
                    taskItem = it.taskItem
                ) {
                    mainLevelRouteStack.removeLast()
                }
            }
            entry<TaskList> {
                TaskScreen(
                    dataTableName = it.dataTableName,
                    onBack = {
                        mainLevelRouteStack.removeLast()
                    }
                ) { dataTableName, taskItem ->
                    mainLevelRouteStack.addTopLevel(
                        TaskMapView(
                            projectId = it.projectId,
                            dataTableName = dataTableName,
                            taskItem
                        )
                    )
                }
            }
        }

    )


}

