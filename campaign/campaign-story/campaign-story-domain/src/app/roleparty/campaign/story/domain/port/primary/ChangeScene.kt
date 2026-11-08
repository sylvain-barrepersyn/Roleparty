package app.roleparty.campaign.story.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.story.commons.SceneId
import app.roleparty.campaign.story.domain.model.StoryError
import app.roleparty.core.commons.Outcome

interface ChangeScene {

    suspend operator fun invoke(requester: AccountId, campaignId: CampaignId, sceneId: SceneId, name: String, description: String): Outcome<Unit, StoryError>
}
