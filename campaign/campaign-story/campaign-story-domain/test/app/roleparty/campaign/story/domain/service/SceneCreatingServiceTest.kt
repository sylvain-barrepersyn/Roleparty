package app.roleparty.campaign.story.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.story.domain.model.StoryError
import app.roleparty.campaign.story.test.StoryBehaviorTest
import app.roleparty.campaign.story.test.document
import app.roleparty.core.document.Document
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe

class SceneCreatingServiceTest : StoryBehaviorTest() {

    init {
        Given("a campaign with a player") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            val player = AccountId.generate()

            When("the game master creates a scene") {
                val sceneId = fixtures.createScene(gameMaster, campaignId, "Ambush", document("Goblins.")).shouldBeSuccess()

                Then("the scene should exist") {
                    fixtures.scene(sceneId).shouldNotBeNull().name shouldBe "Ambush"
                }
            }

            Then("a player or a blank name should be refused") {
                fixtures.createScene(player, campaignId, "Ambush", Document.EMPTY) shouldBeFailure StoryError.GameMasterRequired
                fixtures.createScene(gameMaster, campaignId, "", Document.EMPTY) shouldBeFailure StoryError.BlankName
            }
        }
    }
}
