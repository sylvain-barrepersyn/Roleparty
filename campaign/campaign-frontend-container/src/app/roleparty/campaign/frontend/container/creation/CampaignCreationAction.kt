package app.roleparty.campaign.frontend.container.creation

import app.roleparty.core.frontend.container.ViewModelAction
import app.roleparty.media.commons.ImageId

sealed interface CampaignCreationAction : ViewModelAction {

    data class ChangeName(val name: String) : CampaignCreationAction

    data class ChangeDescription(val description: String) : CampaignCreationAction

    data class ChangeImage(val imageId: ImageId) : CampaignCreationAction

    data object Submit : CampaignCreationAction

    data object Cancel : CampaignCreationAction
}
