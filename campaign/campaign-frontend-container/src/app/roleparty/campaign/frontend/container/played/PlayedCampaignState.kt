package app.roleparty.campaign.frontend.container.played

import app.roleparty.campaign.creature.contract.web.PlayerCharacterResponseBody
import app.roleparty.campaign.frontend.component.played.PlayedCampaignSection
import app.roleparty.core.frontend.container.ViewModelState
import app.roleparty.core.ktor.client.BackendFailure

sealed interface PlayedCampaignState : ViewModelState {

    data object Loading : PlayedCampaignState

    data class Loaded(val character: PlayerCharacterResponseBody, val section: PlayedCampaignSection = PlayedCampaignSection.AGENDA) : PlayedCampaignState

    data class Failed(val failure: BackendFailure) : PlayedCampaignState
}
