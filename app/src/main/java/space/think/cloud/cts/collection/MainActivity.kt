package space.think.cloud.cts.collection

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import space.think.cloud.cts.collection.ui.screens.MainScreen
import space.think.cloud.cts.collection.ui.theme.CtsCollectionTheme
import space.think.cloud.cts.lib.form.FormScreen
import space.think.cloud.cts.lib.form.viewmodel.FormViewModel
import space.think.cloud.cts.lib.ui.CircleText
import space.think.cloud.cts.lib.watermark.Margin
import space.think.cloud.cts.lib.watermark.Padding
import space.think.cloud.cts.lib.watermark.Watermark

class MainActivity : ComponentActivity() {
    @SuppressLint("LocalContextResourcesRead")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 隐藏bar
        actionBar?.hide()
        enableEdgeToEdge()
        setContent {
            CtsCollectionTheme {
                Greeting(
                    modifier = Modifier.fillMaxSize()
                )
//                val context = LocalContext.current
//                var bitmap by remember {
//                    mutableStateOf<Bitmap?>(null)
//                }
//                // 准备表格数据
//                val tableData = listOf(
//                    listOf("名  称:", "1"),
//                    listOf(
//                        "项目A:",
//                        "1"
//                    ),
//                    listOf("项目B:", "1"),
//                    listOf("项目C:", "1")
//                )
//                val scope = rememberCoroutineScope()
//                LaunchedEffect(Unit) {
//                    scope.launch {
//                        bitmap = Watermark(
//                            originalBitmap = BitmapFactory.decodeResource(
//                                context.resources,
//                                R.drawable.bg
//                            ),
//                            tableData = tableData,
//                            headerTitle = "实施日志",
//                            margin = Margin(150f),
//                            textVerticalCenter = true,
//                            cellPadding = Padding(30f, 10f)
//                        ).draw()
//                    }
//                }
//
//                bitmap?.apply {
//                    Image(
//                        this.asImageBitmap(),
//                        contentDescription = null
//                    )
//                }

            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun Greeting( modifier: Modifier = Modifier) {
//    val viewModel: FormViewModel = viewModel()
//    FormScreen(modifier = modifier, title = "表单界面", viewModel = viewModel)
    MainScreen(modifier = modifier)
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CircleText(
        modifier = Modifier.padding(20.dp),
        text = "日",
        backgroundColor = Color.Blue
    )
}