package space.think.cloud.cts.collection.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import space.think.cloud.cts.collection.nav.Dashboard
import space.think.cloud.cts.collection.nav.Login
import space.think.cloud.cts.collection.nav.Me
import space.think.cloud.cts.collection.nav.Project
import space.think.cloud.cts.collection.nav.TaskList
import space.think.cloud.cts.collection.nav.TopLevelBackStack
import space.think.cloud.cts.collection.nav.TopLevelRoute

// Home 菜单页面
private val HOME_LEVEL_ROUTES: List<TopLevelRoute> = listOf(Project, Dashboard, Me)

/**
 * 该界面是程序的菜单界面，包含底部菜单页
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    mainLevelRouteStack: TopLevelBackStack<Any>,
    topLevelBackStack: TopLevelBackStack<Any>,
) {

    Scaffold(

        bottomBar = {
            NavigationBar {
                HOME_LEVEL_ROUTES.forEach { topLevelRoute ->

                    val isSelected = topLevelRoute == topLevelBackStack.topLevelKey

                    NavigationBarItem(
                        icon = {
                            Icon(
                                if (isSelected) topLevelRoute.selectIcon else topLevelRoute.unSelectIcon,
                                contentDescription = null
                            )
                        },
                        label = { Text(topLevelRoute.name) },
                        selected = isSelected,
                        onClick = {
                            topLevelBackStack.addTopLevel(topLevelRoute)
                        }
                    )
                }
            }
        }
    ) { paddingValue ->

        NavDisplay(
            backStack = topLevelBackStack.backStack,
            onBack = { topLevelBackStack.removeLast() },
            entryProvider = entryProvider {
                entry<Project> {
                    ProjectScreen(
                        modifier = modifier.padding(paddingValue),
                    ){
                        // 切换到任务列表中
                        mainLevelRouteStack.addTopLevel(TaskList(projectId = it.id))
                    }
                }
                entry<Dashboard> {
                    DashboardScreen(modifier.padding(paddingValue))
                }
                entry<Me> {
                    ProfileScreen(modifier.padding(paddingValue)){
                        // 切换到登录页
                        mainLevelRouteStack.addTopLevel(Login)
                    }
                }
            }

        )

    }
}