package app.roleparty.campaign.frontend.container.mastered

import app.roleparty.campaign.frontend.component.mastered.MasteredCampaignSection
import app.roleparty.core.frontend.container.ViewModelAction

sealed interface MasteredCampaignAction : ViewModelAction {

    data object Retry : MasteredCampaignAction

    data class ShowSection(val section: MasteredCampaignSection) : MasteredCampaignAction

    data object Exit : MasteredCampaignAction
}
