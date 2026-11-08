package app.roleparty.campaign.frontend.container.settings

import app.roleparty.campaign.contract.web.InvitationResponseBody
import app.roleparty.core.frontend.container.ViewModelState
import app.roleparty.core.ktor.client.BackendFailure

sealed interface CampaignSettingsState : ViewModelState {

    data object Loading : CampaignSettingsState

    data class Loaded(
        val invitations: List<InvitationResponseBody>,
        val generating: Boolean = false,
        val failure: BackendFailure? = null,
    ) : CampaignSettingsState

    data class Failed(val failure: BackendFailure) : CampaignSettingsState
}
