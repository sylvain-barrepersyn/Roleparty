package app.roleparty.campaign.tabletop.frontend.component

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import app.roleparty.campaign.tabletop.contract.web.StartedGameSessionBody
import app.roleparty.campaign.tabletop.frontend.component.resources.Res
import app.roleparty.campaign.tabletop.frontend.component.resources.tabletop_title
import app.roleparty.core.designsystem.surface.BaseSurface
import org.jetbrains.compose.resources.stringResource

private val WINDOW_SIZE = DpSize(1100.dp, 720.dp)

@Composable
fun TabletopWindow(gameSession: StartedGameSessionBody, onCloseRequest: () -> Unit, content: @Composable BoxScope.() -> Unit) {
    Window(
        onCloseRequest = onCloseRequest,
        state = rememberWindowState(size = WINDOW_SIZE),
        title = stringResource(Res.string.tabletop_title, gameSession.label()),
    ) {
        BaseSurface(modifier = Modifier.fillMaxSize(), content = content)
    }
}
