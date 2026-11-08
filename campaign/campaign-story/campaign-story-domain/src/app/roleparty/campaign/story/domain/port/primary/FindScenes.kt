package app.roleparty.campaign.story.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.story.domain.model.StoryError
import app.roleparty.campaign.story.domain.model.Scene
import app.roleparty.core.commons.Outcome

interface FindScenes {

    suspend fun ofCampaign(requester: AccountId, campaignId: CampaignId): Outcome<List<Scene>, StoryError>
}
