package app.roleparty.campaign.story.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.story.commons.SceneId
import app.roleparty.campaign.story.domain.model.StoryError
import app.roleparty.campaign.story.domain.port.primary.DeleteScene
import app.roleparty.campaign.story.domain.port.secondary.Scenes
import app.roleparty.core.commons.Outcome

internal class SceneDeletingService(
    private val scenes: Scenes,
    private val gameMaster: GameMasterCheckingService,
) : DeleteScene {

    override suspend fun invoke(requester: AccountId, campaignId: CampaignId, sceneId: SceneId): Outcome<Unit, StoryError> {
        gameMaster.check(requester, campaignId)?.let { return Outcome.Failure(it) }

        scenes.findById(sceneId)?.takeIf { it.campaignId == campaignId } ?: return Outcome.Failure(StoryError.SceneNotFound(sceneId))
        scenes.delete(sceneId)
        return Outcome.success()
    }
}
