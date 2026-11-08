package app.roleparty.core.frontend.container

import androidx.compose.runtime.Composable
import app.roleparty.core.frontend.container.resources.Res
import app.roleparty.core.frontend.container.resources.unexpected
import app.roleparty.core.frontend.container.resources.unreachable
import app.roleparty.core.ktor.client.BackendFailure
import org.jetbrains.compose.resources.stringResource

@Composable
fun BackendFailure.message(): String = when (this) {
    is BackendFailure.Rejected -> message
    BackendFailure.Unreachable -> stringResource(Res.string.unreachable)
    BackendFailure.Unexpected -> stringResource(Res.string.unexpected)
}
