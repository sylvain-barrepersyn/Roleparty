package app.roleparty.campaign.frontend.container.creation

import app.roleparty.core.frontend.container.ViewModelState
import app.roleparty.core.ktor.client.BackendFailure
import app.roleparty.media.commons.ImageId

sealed interface CampaignCreationState : ViewModelState {

    data class Form(
        val name: String = "",
        val description: String = "",
        val imageId: ImageId? = null,
        val submitting: Boolean = false,
        val failure: BackendFailure? = null,
    ) : CampaignCreationState {

        val complete get() = name.isNotBlank() && imageId != null
    }
}
