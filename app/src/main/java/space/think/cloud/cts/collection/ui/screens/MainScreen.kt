package space.think.cloud.cts.collection.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import space.think.cloud.cts.collection.nav.TopLevelBackStack
import space.think.cloud.cts.lib.ui.R

private data object Login
private data object Home
private data object Help

data class TaskList(val projectId: String)

/**
 * 该界面是主界面，
 */
@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val topLevelBackStack = remember { TopLevelBackStack<Any>(Home) }
    NavDisplay(
        backStack = topLevelBackStack.backStack,
        onBack = { topLevelBackStack.removeLast() },
        entryProvider = entryProvider {
            entry<Home> {
                HomeScreen(
                    modifier = modifier,
                    mainLevelRouteStack = topLevelBackStack,
                )
            }
            entry<Login> {
                LoginScreen(modifier = modifier, painterResource(R.drawable.shape))
            }
            entry<Help> {
                HelpScreen(modifier = modifier)
            }
            entry<TaskList> {
                TaskListScreen(id = it.projectId, modifier = modifier)
            }
        }

    )


}

