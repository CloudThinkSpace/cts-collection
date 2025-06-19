package space.think.cloud.cts.lib.ui.form

import android.net.Uri
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil3.compose.rememberAsyncImagePainter

/**
 * ClassName: ImageView
 * Description:
 * @date: 2022/10/16 16:32
 * @author: tanghy
 */
@Composable
fun ImageView(
    uri: String? = null,
    title: String? = null,
    size: Dp = 80.dp,
    loading: Boolean = false,
    enabled: Boolean = true,
    isError: Boolean = false,
    fontSize: TextUnit = 10.sp,
    color: Color = Color.Unspecified,
    onPreview: ((Uri) -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onClick: () -> Unit,
) {

    Column(
        modifier = Modifier.padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clickable {
                    onClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(
                    color = if (isError && uri == null) Color.Red else Color.Gray,
                    style = Stroke(
                        width = 3f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 5f), 0f)
                    ),
                    cornerRadius = CornerRadius(10f)
                )
            }

            if (loading) {
                Box(
                    modifier = Modifier
                        .size(size)
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.Gray
                    )
                }
            } else {

                if (uri != null) {
                    Box(
                        contentAlignment = Alignment.TopEnd
                    ) {

                        Image(
                            modifier = Modifier
                                .size(size)
                                .padding(2.dp)
                                .clickable {
                                    onPreview?.invoke(uri.toUri())
                                },
                            contentScale = ContentScale.Crop,
                            painter = rememberAsyncImagePainter(uri.toUri()),
                            contentDescription = "选择的照片"
                        )

                        if (enabled) {
                            IconButton(
                                modifier = Modifier.offset(
                                    x = (20).dp,
                                    y = (-20).dp
                                ),
                                onClick = {
                                    onDelete?.invoke()
                                }) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .background(
                                            color = Color.White,
                                            shape = CircleShape
                                        )
                                        .border(0.5.dp, Color.Gray, CircleShape)
                                        .size(20.dp)
                                        .padding(2.dp)
                                )

                            }
                        }
                    }

                } else {
                    Box(
                        modifier = Modifier
                            .size(size)
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp), color = if (isError) Color.Red else Color.Gray
                        )
                        VerticalDivider(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(1.dp), color = if (isError) Color.Red else Color.Gray
                        )
                    }
                }

            }
        }
        if (title?.isNotBlank() == true) {
            Text(text = title, fontSize = fontSize, color = color)
        }
    }

}
