package space.think.cloud.cts.collection

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import space.think.cloud.cts.collection.ui.screens.MainScreen
import space.think.cloud.cts.collection.ui.theme.CtsCollectionTheme
import space.think.cloud.cts.common.gis.location.CtsLocationManager
import space.think.cloud.cts.lib.ui.CircleText

class MainActivity : ComponentActivity() {


    private lateinit var locationManager: CtsLocationManager

    @SuppressLint("LocalContextResourcesRead")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT//竖屏
        // 隐藏bar
        actionBar?.hide()
        enableEdgeToEdge()
        setContent {
            CtsCollectionTheme {
                Greeting(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        locationManager = CtsLocationManager(this)
        locationManager.checkAndRequestPermissions()
    }



    override fun onDestroy() {
        super.onDestroy()
        // 根据需求决定是否在Activity销毁时停止工作
        locationManager.stopLocationUpdates()
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