package app.roleparty.campaign.frontend.container.creation

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.frontend.component.creation.CampaignCreationForm
import app.roleparty.core.frontend.container.Container
import app.roleparty.core.frontend.container.message
import app.roleparty.media.frontend.container.imagefield.ImageFieldContainer

class CampaignCreationContainer(
    private val onCampaignCreated: (CampaignId) -> Unit,
    private val onCancelled: () -> Unit,
) : Container<CampaignCreationState, CampaignCreationAction, CampaignCreationEvent, CampaignCreationViewModel>(CampaignCreationViewModel::class) {

    @Composable
    override fun Content(state: CampaignCreationState, handle: (CampaignCreationAction) -> Unit) {
        when (state) {
            is CampaignCreationState.Form -> CampaignCreationForm(
                name = state.name,
                description = state.description,
                submitting = state.submitting,
                canSubmit = state.complete,
                error = state.failure?.message(),
                onNameChange = { handle(CampaignCreationAction.ChangeName(it)) },
                onDescriptionChange = { handle(CampaignCreationAction.ChangeDescription(it)) },
                onSubmit = { handle(CampaignCreationAction.Submit) },
                onCancel = { handle(CampaignCreationAction.Cancel) },
                imageField = { label, modifier ->
                    ImageFieldContainer(
                        fieldId = "campaign-creation",
                        label = label,
                        value = state.imageId,
                        onValueChange = { handle(CampaignCreationAction.ChangeImage(it)) },
                        modifier = modifier,
                        imageSize = DpSize(200.dp, 200.dp),
                        required = true,
                    ).Render()
                },
            )
        }
    }

    override fun onEvent(event: CampaignCreationEvent) = when (event) {
        is CampaignCreationEvent.CampaignCreated -> onCampaignCreated(event.campaignId)
        CampaignCreationEvent.CreationCancelled -> onCancelled()
    }
}
