package app.roleparty.campaign.story.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.story.commons.SceneId
import app.roleparty.campaign.story.domain.model.StoryError
import app.roleparty.core.commons.Outcome

interface CreateScene {

    suspend operator fun invoke(requester: AccountId, campaignId: CampaignId, name: String, description: String): Outcome<SceneId, StoryError>
}
