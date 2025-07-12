package space.think.cloud.cts.collection.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import space.think.cloud.cts.collection.nav.TopLevelBackStack

sealed interface TopLevelRoute {
    val selectIcon: ImageVector
    val unSelectIcon: ImageVector
    val name: String
}

// 项目对象
private data object Project : TopLevelRoute {
    override val name: String
        get() = "项目"
    override val selectIcon: ImageVector
        get() = Icons.Filled.Home
    override val unSelectIcon: ImageVector
        get() = Icons.Outlined.Home
}

// 我的对象
private data object Me : TopLevelRoute {
    override val name: String
        get() = "我的"
    override val selectIcon: ImageVector
        get() = Icons.Filled.AccountCircle
    override val unSelectIcon: ImageVector
        get() = Icons.Outlined.AccountCircle
}

// 工具箱
private data object Dashboard : TopLevelRoute {
    override val name: String
        get() = "工具箱"
    override val selectIcon: ImageVector
        get() = Icons.Default.Dashboard
    override val unSelectIcon: ImageVector
        get() = Icons.Outlined.Dashboard
}

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
) {
    val topLevelBackStack = remember { TopLevelBackStack<Any>(Project) }

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
                    MeScreen(modifier.padding(paddingValue))
                }
            }

        )

    }
}