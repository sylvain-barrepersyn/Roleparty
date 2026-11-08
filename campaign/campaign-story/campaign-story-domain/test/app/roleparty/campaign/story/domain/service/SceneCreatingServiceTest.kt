package app.roleparty.campaign.story.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.story.domain.model.StoryError
import app.roleparty.campaign.story.test.StoryBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe
import io.kotest.matchers.nulls.shouldNotBeNull

class SceneCreatingServiceTest : StoryBehaviorTest() {

    init {
        Given("a campaign with a player") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            val player = AccountId.generate()

            When("the game master creates a scene") {
                val sceneId = fixtures.createScene(gameMaster, campaignId, "Ambush", "Goblins.").shouldBeSuccess()

                Then("the scene should exist") {
                    fixtures.scene(sceneId).shouldNotBeNull().name shouldBe "Ambush"
                }
            }

            Then("a player or a blank name should be refused") {
                fixtures.createScene(player, campaignId, "Ambush", "") shouldBeFailure StoryError.GameMasterRequired
                fixtures.createScene(gameMaster, campaignId, "", "") shouldBeFailure StoryError.BlankName
            }
        }
    }
}
