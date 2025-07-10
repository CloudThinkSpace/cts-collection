package space.think.cloud.cts.lib.ui.form

import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import space.think.cloud.cts.lib.photo.PhotoController
import space.think.cloud.cts.lib.ui.CameraBottomSheet

/**
 * ClassName: ImageView
 * Description:
 * @date: 2022/10/16 16:32
 * @author: tanghy
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageView(
    modifier: Modifier = Modifier,
    uri: String? = null,
    title: String? = null,
    size: Dp = 80.dp,
    loading: Boolean = false,
    enabled: Boolean = true,
    isError: Boolean = false,
    fontSize: TextUnit = 10.sp,
    color: Color = Color.Unspecified,
    onClick: (() -> Unit)? = null,
    onPreview: ((Uri) -> Unit)? = null,
    onDelete: (() -> Unit)? = null,
    onChangeValue: (String?) -> Unit,
) {

    val mediaAction by remember {
        mutableStateOf(PhotoController(type = PhotoController.Type.IMAGE))
    }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .clickable {
                    onClick?.invoke()
                    showBottomSheet = true
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRoundRect(
                    color = if (isError && uri == null) Color.Red else Color.Gray,
                    style = Stroke(
                        width = 2f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 5f), 0f)
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

                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(uri)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Video thumbnail",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(size)
                                .padding(2.dp)
                                .clickable {
                                    onPreview?.invoke(uri.toUri())
                                }
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
                                .width(size / 2)
                                .height(1.dp), color = if (isError) Color.Red else Color.Gray
                        )
                        VerticalDivider(
                            modifier = Modifier
                                .height(size / 2)
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

    mediaAction.Register(
        galleryCallback = {
            if (it.isSuccess) {
                onChangeValue( it.uri?.toString())

            }
        },
        graphCallback = {
            if (it.isSuccess) {
                onChangeValue(it.uri?.toString())
            }
        },
        permissionRationale = {
            //权限拒绝的处理
        }
    )

    if (showBottomSheet) {
        ModalBottomSheet(
            shape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp),
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            CameraBottomSheet(
                onCamera = {
                    showBottomSheet = false
                    mediaAction.takePhoto()
                },
                onSelect = {
                    showBottomSheet = false
                    mediaAction.selectImage()
                }
            )
        }
    }

}
