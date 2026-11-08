package app.roleparty.campaign.frontend.container.joining

import androidx.compose.runtime.Composable
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.frontend.component.joining.CampaignJoiningForm
import app.roleparty.core.frontend.container.Container
import app.roleparty.core.frontend.container.message

class CampaignJoiningContainer(
    private val onCampaignJoined: (CampaignId) -> Unit,
    private val onCancelled: () -> Unit,
) : Container<CampaignJoiningState, CampaignJoiningAction, CampaignJoiningEvent, CampaignJoiningViewModel>(CampaignJoiningViewModel::class) {

    @Composable
    override fun Content(state: CampaignJoiningState, handle: (CampaignJoiningAction) -> Unit) {
        when (state) {
            is CampaignJoiningState.Form -> CampaignJoiningForm(
                code = state.code,
                submitting = state.submitting,
                canSubmit = state.code.isNotBlank(),
                error = state.failure?.message(),
                onCodeChange = { handle(CampaignJoiningAction.ChangeCode(it)) },
                onSubmit = { handle(CampaignJoiningAction.Submit) },
                onCancel = { handle(CampaignJoiningAction.Cancel) },
            )
        }
    }

    override fun onEvent(event: CampaignJoiningEvent) = when (event) {
        is CampaignJoiningEvent.CampaignJoined -> onCampaignJoined(event.campaignId)
        CampaignJoiningEvent.JoiningCancelled -> onCancelled()
    }
}
