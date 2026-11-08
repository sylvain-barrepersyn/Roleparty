package app.roleparty.campaign.story.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.story.commons.SceneId
import app.roleparty.campaign.story.domain.model.StoryError
import app.roleparty.campaign.story.domain.port.primary.ChangeScene
import app.roleparty.campaign.story.domain.port.secondary.Scenes
import app.roleparty.core.commons.Outcome

internal class SceneChangingService(
    private val scenes: Scenes,
    private val gameMaster: GameMasterCheckingService,
) : ChangeScene {

    override suspend fun invoke(requester: AccountId, campaignId: CampaignId, sceneId: SceneId, name: String, description: String): Outcome<Unit, StoryError> {
        gameMaster.check(requester, campaignId)?.let { return Outcome.Failure(it) }

        val scene = scenes.findById(sceneId)?.takeIf { it.campaignId == campaignId } ?: return Outcome.Failure(StoryError.SceneNotFound(sceneId))
        if (name.isBlank()) return Outcome.Failure(StoryError.BlankName)

        scenes.save(scene.copy(name = name.trim(), description = description.trim()))
        return Outcome.success()
    }
}
