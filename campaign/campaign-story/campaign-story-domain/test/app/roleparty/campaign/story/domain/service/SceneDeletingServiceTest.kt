package app.roleparty.campaign.story.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.story.domain.model.StoryError
import app.roleparty.campaign.story.test.StoryBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull

class SceneDeletingServiceTest : StoryBehaviorTest() {

    init {
        Given("a scene of a campaign with a player") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            val player = AccountId.generate()
            val sceneId = fixtures.`given a scene`(campaignId)

            When("a player deletes it") {
                Then("it should be refused and the scene kept") {
                    fixtures.deleteScene(player, campaignId, sceneId) shouldBeFailure StoryError.GameMasterRequired
                    fixtures.scene(sceneId).shouldNotBeNull()
                }
            }

            When("the game master deletes it") {
                fixtures.deleteScene(gameMaster, campaignId, sceneId).shouldBeSuccess()

                Then("it should be gone") {
                    fixtures.scene(sceneId).shouldBeNull()
                    fixtures.deleteScene(gameMaster, campaignId, sceneId) shouldBeFailure StoryError.SceneNotFound(sceneId)
                }
            }
        }
    }
}
