package space.think.cloud.cts.lib.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun ActionIcon(
    onClick: () -> Unit,
    pointer: ImageVector
) {
    IconButton(
        onClick = onClick
    ) {
        Icon(
            imageVector = pointer,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}