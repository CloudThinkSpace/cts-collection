package space.think.cloud.cts.lib.ui.form.widgets.layout

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout

/**
 * ClassName: FlowLayout
 * Description:
 * @date: 2023/1/12 22:10
 * @author: tanghy
 */
@Composable
fun FlowLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        // 测量每个项目并将其转换为 Placeable
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }
        val width = constraints.maxWidth
        // 获取所有组件中最大高度
        val maxHeight = placeables.maxOf { it.height }
        // 计算总共的行数
        val rowSet = mutableSetOf<Int>()
        var currentRowWidth = 0
        // 遍历组件，计算数组需要的行数
        placeables.mapIndexed { index, placeable ->
            currentRowWidth += placeable.width
            if (currentRowWidth > width) {
                currentRowWidth = placeable.width
                rowSet.add(index)
            }
        }
        // 计算组件的高度
        val height = maxHeight * (rowSet.size + 1)
        // 报告所需的尺寸
        layout(width, height) {
            // 通过跟踪 y 坐标放置每个项目
            var x = 0
            var y = 0
            placeables.mapIndexed { index, placeable ->

                if (rowSet.contains(index)) {
                    y += maxHeight
                    x = 0
                }

                placeable.placeRelative(x = x, y = y)
                x += placeable.width

            }
        }
    }
}