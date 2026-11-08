package app.roleparty.core.frontend.container

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.roleparty.core.designsystem.button.Button
import app.roleparty.core.designsystem.button.ButtonSize
import app.roleparty.core.designsystem.feedback.Callout
import app.roleparty.core.designsystem.feedback.CalloutKind
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.layout.CenteredBox
import app.roleparty.core.frontend.container.resources.Res
import app.roleparty.core.frontend.container.resources.retry
import app.roleparty.core.ktor.client.BackendFailure
import org.jetbrains.compose.resources.stringResource

@Composable
fun RetryableFailure(failure: BackendFailure, onRetry: () -> Unit) {
    CenteredBox {
        Callout(text = failure.message(), kind = CalloutKind.ERROR, modifier = Modifier.widthIn(max = 600.dp).padding(24.dp)) {
            Button(text = stringResource(Res.string.retry), onClick = onRetry, leadingIcon = IconVector.REFRESH, size = ButtonSize.SMALL)
        }
    }
}
