package app.roleparty.campaign.story.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.story.domain.model.StoryError
import app.roleparty.campaign.story.domain.model.Scene
import app.roleparty.campaign.story.domain.port.primary.FindScenes
import app.roleparty.campaign.story.domain.port.secondary.Scenes
import app.roleparty.core.commons.Outcome

internal class SceneFindingService(
    private val scenes: Scenes,
    private val gameMaster: GameMasterCheckingService,
) : FindScenes {

    override suspend fun ofCampaign(requester: AccountId, campaignId: CampaignId): Outcome<List<Scene>, StoryError> {
        gameMaster.check(requester, campaignId)?.let { return Outcome.Failure(it) }

        return Outcome.Success(scenes.ofCampaign(campaignId))
    }
}
