package app.roleparty.application.frontend

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.frontend.container.creation.CampaignCreationContainer
import app.roleparty.campaign.frontend.container.joining.CampaignJoiningContainer
import app.roleparty.campaign.frontend.container.mastered.MasteredCampaignContainer
import app.roleparty.campaign.frontend.container.selection.CampaignSelectionContainer
import app.roleparty.core.frontend.container.Navigation
import app.roleparty.security.frontend.component.authentication.SignOutButton

private sealed interface Screen {

    data object Campaigns : Screen

    data object CampaignCreation : Screen

    data object CampaignJoining : Screen

    data class MasteredCampaign(val campaignId: CampaignId) : Screen
}

@Composable
internal fun Screens(signOut: () -> Unit) {
    val backStack = remember { mutableStateListOf<Screen>(Screen.Campaigns) }

    fun back() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }

    fun replaceTop(screen: Screen) {
        backStack[backStack.lastIndex] = screen
    }

    Navigation(backStack = backStack, onBack = ::back) { screen ->
        when (screen) {
            Screen.Campaigns -> Centered {
                CampaignSelectionContainer(
                    onMasteredCampaignOpened = { backStack.add(Screen.MasteredCampaign(it)) },
                    onCampaignCreationRequested = { backStack.add(Screen.CampaignCreation) },
                    onCampaignJoiningRequested = { backStack.add(Screen.CampaignJoining) },
                    footer = { SignOutButton(onClick = signOut) },
                ).Render()
            }

            Screen.CampaignCreation -> Centered {
                CampaignCreationContainer(onCampaignCreated = { replaceTop(Screen.MasteredCampaign(it)) }, onCancelled = ::back).Render()
            }

            Screen.CampaignJoining -> Centered {
                CampaignJoiningContainer(onCampaignJoined = { back() }, onCancelled = ::back).Render()
            }

            is Screen.MasteredCampaign -> MasteredCampaignContainer(screen.campaignId, onExited = ::back).Render()
        }
    }
}

@Composable
private fun Centered(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) { content() }
}
