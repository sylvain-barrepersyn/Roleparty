package app.roleparty.campaign.frontend.container.settings

import androidx.compose.runtime.Composable
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.contract.web.CampaignResponseBody
import app.roleparty.campaign.frontend.component.settings.CampaignSettings
import app.roleparty.core.frontend.container.RetryableFailure
import app.roleparty.core.frontend.container.Container
import app.roleparty.core.frontend.container.message

class CampaignSettingsContainer(
    campaignId: CampaignId,
    private val campaign: CampaignResponseBody,
) : Container<CampaignSettingsState, CampaignSettingsAction, CampaignSettingsEvent, CampaignSettingsViewModel>(CampaignSettingsViewModel::class, campaignId) {

    @Composable
    override fun Content(state: CampaignSettingsState, handle: (CampaignSettingsAction) -> Unit) {
        when (state) {
            is CampaignSettingsState.Failed -> RetryableFailure(state.failure) { handle(CampaignSettingsAction.Retry) }
            CampaignSettingsState.Loading, is CampaignSettingsState.Loaded -> {
                val loaded = state as? CampaignSettingsState.Loaded
                CampaignSettings(
                    campaign = campaign,
                    invitations = loaded?.invitations,
                    generating = loaded?.generating == true,
                    error = loaded?.failure?.message(),
                    onGenerate = { handle(CampaignSettingsAction.GenerateInvitation) },
                )
            }
        }
    }
}
