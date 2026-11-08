package app.roleparty.application.frontend

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import app.roleparty.application.frontend.resources.Res
import app.roleparty.application.frontend.resources.quit_application
import app.roleparty.application.frontend.resources.resume_application
import app.roleparty.core.designsystem.button.Button
import app.roleparty.core.designsystem.dialog.Dialog
import app.roleparty.core.designsystem.icon.IconVector
import org.jetbrains.compose.resources.stringResource

@Composable
fun QuitDialogOnEscape(onQuit: () -> Unit) {
    var displayed by remember { mutableStateOf(false) }

    NavigationBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        onBackCompleted = { displayed = true },
    )

    Dialog(
        displayed = displayed,
        onDismiss = { displayed = false },
        modifier = Modifier.width(320.dp),
    ) {
        Column(Modifier.padding(16.dp), Arrangement.spacedBy(8.dp)) {
            Button(
                leadingIcon = IconVector.PLAY,
                text = stringResource(Res.string.resume_application),
                onClick = { displayed = false },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                leadingIcon = IconVector.CANCEL,
                text = stringResource(Res.string.quit_application),
                onClick = onQuit,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
