package space.think.cloud.cts.lib.form

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import space.think.cloud.cts.lib.ui.MultiImageViewer

class ImagePreviewActivity : ComponentActivity() {
    @SuppressLint("LocalContextResourcesRead")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 接收方 (TargetActivity)
        val uriStrings = intent.getStringArrayListExtra("uris")
        val uriList = uriStrings?.map { it.toUri() } ?: emptyList()
        // 隐藏bar
        actionBar?.hide()
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                MultiImageViewer(
                    modifier = Modifier.fillMaxSize(),
                    images = uriList,
                    initialIndex = 0,
                ) {
                    finish()
                }
            }
        }
    }
}