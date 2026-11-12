package app.roleparty.core.designsystem.toast

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ToastHost(modifier: Modifier = Modifier) {
    val toasts by Toast.toasts.collectAsState()

    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.End,
    ) {
        toasts.forEach { toast ->
            key(toast.id) {
                ToastItem(toast = toast, onDismiss = { Toast.remove(toast.id) })
            }
        }
    }
}
