package app.roleparty.application.frontend

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import app.roleparty.core.designsystem.surface.BaseSurface
import app.roleparty.core.designsystem.theme.RolepartyTheme
import app.roleparty.campaign.frontend.container.campaignFrontendModule
import app.roleparty.media.frontend.container.mediaFrontendModule
import app.roleparty.security.frontend.container.authentication.AuthenticationContainer
import app.roleparty.security.frontend.container.securityFrontendModule
import java.awt.Desktop
import java.awt.GraphicsEnvironment
import kotlinx.coroutines.flow.filter
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

fun main() {
    ignoreSystemQuitRequests()

    application {
        Window(
            onCloseRequest = {},
            state = rememberLockedFullscreenWindowState(),
            title = "Roleparty",
            undecorated = true,
        ) {
            RolepartyTheme {
                BaseSurface(Modifier.fillMaxSize()) {
                    QuitDialogOnEscape(onQuit = ::exitApplication)

                    KoinApplication(
                        configuration = koinConfiguration { modules(securityFrontendModule, mediaFrontendModule, campaignFrontendModule) },
                    ) {
                        val authentication = remember { AuthenticationContainer { signOut -> Screens(signOut) } }
                        authentication.Render()
                    }
                }
            }
        }
    }
}

private fun ignoreSystemQuitRequests() {
    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.APP_QUIT_HANDLER)) {
        Desktop.getDesktop().setQuitHandler { _, response -> response.cancelQuit() }
    }
}

@Composable
private fun rememberLockedFullscreenWindowState(): WindowState {
    val screen = remember { GraphicsEnvironment.getLocalGraphicsEnvironment().defaultScreenDevice.defaultConfiguration.bounds }
    val state = rememberWindowState(
        placement = WindowPlacement.Fullscreen,
        position = WindowPosition(screen.x.dp, screen.y.dp),
        size = DpSize(screen.width.dp, screen.height.dp),
    )

    LaunchedEffect(state) {
        snapshotFlow { state.placement }
            .filter { it != WindowPlacement.Fullscreen }
            .collect { state.placement = WindowPlacement.Fullscreen }
    }

    return state
}
