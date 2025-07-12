package space.think.cloud.cts.collection.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector


sealed interface TopLevelRoute {
    val selectIcon: ImageVector
    val unSelectIcon: ImageVector
    val name: String
}

// 项目对象
data object Project : TopLevelRoute {
    override val name: String
        get() = "项目"
    override val selectIcon: ImageVector
        get() = Icons.Filled.Home
    override val unSelectIcon: ImageVector
        get() = Icons.Outlined.Home
}

// 我的对象
data object Me : TopLevelRoute {
    override val name: String
        get() = "我的"
    override val selectIcon: ImageVector
        get() = Icons.Filled.AccountCircle
    override val unSelectIcon: ImageVector
        get() = Icons.Outlined.AccountCircle
}

// 工具箱
data object Dashboard : TopLevelRoute {
    override val name: String
        get() = "工具箱"
    override val selectIcon: ImageVector
        get() = Icons.Default.Dashboard
    override val unSelectIcon: ImageVector
        get() = Icons.Outlined.Dashboard
}