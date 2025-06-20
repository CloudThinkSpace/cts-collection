package space.think.cloud.cts.collection

import android.annotation.SuppressLint
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
import androidx.lifecycle.viewmodel.compose.viewModel
import space.think.cloud.cts.collection.ui.theme.CtsCollectionTheme
import space.think.cloud.cts.lib.form.FormScreen
import space.think.cloud.cts.lib.form.viewmodel.FormViewModel
import space.think.cloud.cts.lib.ui.CircleText

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 隐藏bar
        actionBar?.hide()
        enableEdgeToEdge()
        setContent {
            CtsCollectionTheme {
                Greeting(
                    name = "Android",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val viewModel: FormViewModel = viewModel()
    FormScreen(modifier = modifier, title = "表单界面", viewModel = viewModel)
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