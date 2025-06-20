package space.think.cloud.cts.lib.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// 虚线边框
fun Modifier.dashBorder(
    color: Color = Color.Gray,
    width: Dp = 1.dp,
    cornerRadiusDp: Dp = 5.dp,
    dashLength: Dp = 5.dp,
    gapLength: Dp = 3.dp
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

// 带动画的虚线边框
fun Modifier.animDashBorder(
    color: Color = Color.Gray,
    width: Dp = 1.dp,
    cornerRadiusDp: Dp = 0.dp,
    dashLength: Dp = 20.dp,
    gapLength: Dp = 5.dp
) = composed {
    // 不在drawScope 中，无法直接使用 dp.toPx()
    val density = LocalDensity.current
    val dashLengthPx = density.run { dashLength.toPx() }
    // 声明一个无限循环动画
    val infinite = rememberInfiniteTransition()
    val anim by infinite.animateFloat(
        initialValue = 0f,
        targetValue = dashLengthPx * 2,//偏移一个完整长度
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart // 动画循环模式为 restart
        )
    )

    drawBehind {
        drawRoundRect(
            color = color,
            style = Stroke(
                width = width.toPx(),
                pathEffect = PathEffect.dashPathEffect(
                    intervals = floatArrayOf(dashLength.toPx(), dashLength.toPx()),
                    phase = anim // 动画应用
                )
            ),
            cornerRadius = CornerRadius(cornerRadiusDp.toPx())
        )
    }
}