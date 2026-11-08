package app.roleparty.campaign.story.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.story.commons.SceneId
import app.roleparty.campaign.story.domain.model.StoryError
import app.roleparty.campaign.story.domain.model.Scene
import app.roleparty.campaign.story.domain.port.primary.CreateScene
import app.roleparty.campaign.story.domain.port.secondary.Scenes
import app.roleparty.core.commons.Outcome

internal class SceneCreatingService(
    private val scenes: Scenes,
    private val gameMaster: GameMasterCheckingService,
) : CreateScene {

    override suspend fun invoke(requester: AccountId, campaignId: CampaignId, name: String, description: String): Outcome<SceneId, StoryError> {
        gameMaster.check(requester, campaignId)?.let { return Outcome.Failure(it) }
        if (name.isBlank()) return Outcome.Failure(StoryError.BlankName)

        val scene = Scene(SceneId.generate(), campaignId, name.trim(), description.trim())
        scenes.save(scene)
        return Outcome.Success(scene.id)
    }
}
