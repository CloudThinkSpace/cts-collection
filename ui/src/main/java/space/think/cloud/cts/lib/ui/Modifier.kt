package space.think.cloud.cts.lib.ui

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

fun Modifier.dashBorder(
    color: Color = Color.Gray,
    width: Dp = 1.dp,
    cornerRadiusDp: Dp = 0.dp,
    dashLength: Dp = 20.dp,
    gapLength: Dp = 5.dp
) = drawBehind {
    drawRoundRect(
        color = color,
        style = Stroke(
            width = width.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                // 简单起见，让空白和线段的长度相同
                intervals = floatArrayOf(dashLength.toPx(), gapLength.toPx()),
                phase = 0f
            )
        ),
        cornerRadius = CornerRadius(cornerRadiusDp.toPx())
    )

}