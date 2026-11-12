package app.roleparty.application.frontend

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.singleWindowApplication
import app.roleparty.campaign.frontend.container.campaignFrontendModule
import app.roleparty.core.designsystem.surface.BaseSurface
import app.roleparty.core.designsystem.theme.RolepartyTheme
import app.roleparty.core.designsystem.toast.ToastHost
import app.roleparty.media.frontend.container.mediaFrontendModule
import app.roleparty.security.frontend.container.authentication.AuthenticationContainer
import app.roleparty.security.frontend.container.securityFrontendModule
import org.koin.compose.KoinApplication
import org.koin.dsl.koinConfiguration

fun main() {
    singleWindowApplication {
        RolepartyTheme {
            BaseSurface(Modifier.fillMaxSize()) {
                KoinApplication(
                    configuration = koinConfiguration {
                        modules(
                            securityFrontendModule,
                            mediaFrontendModule,
                            campaignFrontendModule
                        )
                    },
                ) {
                    val authentication = remember { AuthenticationContainer { signOut -> Screens(signOut) } }
                    authentication.Render()
                }
                ToastHost(Modifier.align(Alignment.BottomEnd))
            }
        }
    }
}
