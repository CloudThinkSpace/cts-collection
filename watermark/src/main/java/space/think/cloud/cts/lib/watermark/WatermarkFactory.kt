package space.think.cloud.cts.lib.watermark

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint

object WatermarkFactory {

    suspend fun create(
        watermark: IWatermark,
        originBitmap: Bitmap,
        margin: Margin = Margin(20f),
        position: Position = LeftBottom,
        rotation: Rotation = Rotation.ANGLE_0
    ): Bitmap {

        // 绘制水印bitmap
        val bitmap = watermark.draw()


        // 创建图像副本
        val dest = originBitmap.copy(Bitmap.Config.ARGB_8888, true)
        // 创建基于目标Bitmap的Canvas
        val canvas = Canvas(dest)

        // 配置画笔
        val paint = Paint().apply {
            isAntiAlias = true // 抗锯齿
        }

        // 创建矩阵用于旋转
        val matrix = Matrix()

        // 1. 平移到绘制位置（旋转中心）
        matrix.postTranslate(-bitmap.width / 2f, -bitmap.height / 2f)
        // 2. 执行旋转
        matrix.postRotate(rotation.angle)
        // 3. 移动回原来位置
        if (rotation == Rotation.ANGLE_0 || rotation == Rotation.ANGLE_180 || rotation == Rotation.ANGLE_360) {
            matrix.postTranslate(bitmap.width / 2f, bitmap.height / 2f)
        } else {
            matrix.postTranslate(bitmap.height / 2f, bitmap.width / 2f)
        }

        // 4. 是否缩放水印
        val scale = 2 / 3f
        // 判断是否达到缩放级别
        var isScale = false
        if (originBitmap.width * scale < bitmap.width || originBitmap.height * scale < bitmap.height) {
            matrix.postScale(scale, scale)
            isScale = true
        }

        // 5. 平移到指定位置
        // 计算水印位置
        val currentPosition =
            calculatingWatermarkPosition(
                originBitmap,
                bitmap,
                margin,
                position,
                rotation,
                if (isScale) scale else 1f
            )
        // 水印起始坐标
        val tableLeft = currentPosition.first
        val tableTop = currentPosition.second
        matrix.postTranslate(tableLeft, tableTop)


        // 绘制源Bitmap到目标Bitmap
        canvas.drawBitmap(bitmap, matrix, paint)

        return dest
    }

    /**
     * 计算水印的位置，将目标图片绘制到源图片上
     * @param originalBitmap 源图片
     * @param destBitmap 目标图片
     * @param margin 边距
     * @param position 水印的位置方向
     * @param rotation 目标图片旋转角度
     * @param scale 目标图片缩放比例
     */
    private fun calculatingWatermarkPosition(
        originalBitmap: Bitmap,
        destBitmap: Bitmap,
        margin: Margin,
        position: Position,
        rotation: Rotation,
        scale: Float,
    ): Pair<Float, Float> {

        val tableWidth = destBitmap.width * scale
        val tableHeight = destBitmap.height * scale
        // 计算表格位置
        var tableLeft = 0f
        var tableTop = 0f
        when (position) {
            is LeftTop -> {
                tableLeft = margin.left
                tableTop = margin.top
            }

            is RightTop -> {
                tableLeft =
                    if (rotation == Rotation.ANGLE_0 || rotation == Rotation.ANGLE_180 || rotation == Rotation.ANGLE_360) {
                        originalBitmap.width - tableWidth - margin.right
                    } else {
                        originalBitmap.width - tableHeight - margin.right
                    }
                tableTop = margin.top
            }

            is LeftBottom -> {
                tableTop =
                    if (rotation == Rotation.ANGLE_0 || rotation == Rotation.ANGLE_180 || rotation == Rotation.ANGLE_360) {
                        originalBitmap.height - tableHeight - margin.bottom
                    } else {
                        originalBitmap.height - tableWidth - margin.bottom
                    }
                tableLeft = margin.left
            }

            is RightBottom -> {
                if (rotation == Rotation.ANGLE_0 || rotation == Rotation.ANGLE_180 || rotation == Rotation.ANGLE_360) {
                    tableLeft = originalBitmap.width - tableWidth - margin.right
                    tableTop = originalBitmap.height - tableHeight - margin.bottom
                } else {
                    tableLeft = originalBitmap.width - tableHeight - margin.right
                    tableTop = originalBitmap.height - tableWidth - margin.bottom
                }

            }

            is TopCenter -> {
                tableLeft =
                    if (rotation == Rotation.ANGLE_0 || rotation == Rotation.ANGLE_180 || rotation == Rotation.ANGLE_360) {
                        (originalBitmap.width - tableWidth) / 2
                    } else {
                        (originalBitmap.width - tableHeight) / 2
                    }
                tableTop = margin.top
            }

            is BottomCenter -> {
                if (rotation == Rotation.ANGLE_0 || rotation == Rotation.ANGLE_180 || rotation == Rotation.ANGLE_360) {
                    tableLeft = (originalBitmap.width - tableWidth) / 2
                    tableTop = originalBitmap.height - tableHeight - margin.bottom
                } else {
                    tableLeft = (originalBitmap.width - tableHeight) / 2
                    tableTop = originalBitmap.height - tableWidth - margin.bottom
                }
            }

            is LeftCenter -> {
                tableTop =
                    if (rotation == Rotation.ANGLE_0 || rotation == Rotation.ANGLE_180 || rotation == Rotation.ANGLE_360) {
                        (originalBitmap.height - tableHeight) / 2
                    } else {
                        (originalBitmap.height - tableWidth) / 2
                    }
                tableLeft = margin.left
            }

            is RightCenter -> {
                if (rotation == Rotation.ANGLE_0 || rotation == Rotation.ANGLE_180 || rotation == Rotation.ANGLE_360) {
                    tableLeft = originalBitmap.width - tableWidth - margin.right
                    tableTop = (originalBitmap.height - tableHeight) / 2
                } else {
                    tableLeft = originalBitmap.width - tableHeight - margin.right
                    tableTop = (originalBitmap.height - tableWidth) / 2
                }


            }

            is Center -> {
                tableLeft = (originalBitmap.width - tableWidth) / 2
                tableTop = (originalBitmap.height - tableHeight) / 2
            }

            is CustomPosition -> {
                tableLeft = position.x
                tableTop = position.y
            }
        }

        return Pair(tableLeft, tableTop)
    }
}