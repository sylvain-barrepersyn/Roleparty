package app.roleparty.campaign.creature.frontend.container.playercharacter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.creature.frontend.component.playercharacter.PlayerCharacterSelection
import app.roleparty.core.frontend.container.Container
import app.roleparty.core.frontend.container.message
import app.roleparty.media.frontend.container.data.MediaApi
import org.koin.compose.koinInject

class PlayerCharacterSelectionContainer(
    campaignId: CampaignId,
    private val onPlayerCharacterOpened: (PlayerCharacterId) -> Unit,
    private val onPlayerCharacterCreationRequested: () -> Unit,
    private val onExited: () -> Unit,
) : Container<PlayerCharacterSelectionState, PlayerCharacterSelectionAction, PlayerCharacterSelectionEvent, PlayerCharacterSelectionViewModel>(
    PlayerCharacterSelectionViewModel::class,
    campaignId,
) {

    @Composable
    override fun Content(state: PlayerCharacterSelectionState, handle: (PlayerCharacterSelectionAction) -> Unit) {
        val media = koinInject<MediaApi>()
        LaunchedEffect(Unit) { handle(PlayerCharacterSelectionAction.Refresh) }

        PlayerCharacterSelection(
            characters = (state as? PlayerCharacterSelectionState.Loaded)?.characters,
            error = (state as? PlayerCharacterSelectionState.Failed)?.failure?.message(),
            imageUrl = media::imageUrl,
            onSelect = { handle(PlayerCharacterSelectionAction.OpenPlayerCharacter(it)) },
            onCreate = { handle(PlayerCharacterSelectionAction.CreatePlayerCharacter) },
            onBack = { handle(PlayerCharacterSelectionAction.Exit) },
        )
    }

    override fun onEvent(event: PlayerCharacterSelectionEvent) = when (event) {
        is PlayerCharacterSelectionEvent.PlayerCharacterOpened -> onPlayerCharacterOpened(event.playerCharacterId)
        PlayerCharacterSelectionEvent.PlayerCharacterCreationRequested -> onPlayerCharacterCreationRequested()
        PlayerCharacterSelectionEvent.Exited -> onExited()
    }
}
