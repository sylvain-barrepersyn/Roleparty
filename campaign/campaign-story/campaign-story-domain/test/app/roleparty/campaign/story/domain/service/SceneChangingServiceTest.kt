package app.roleparty.campaign.story.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.story.commons.SceneId
import app.roleparty.campaign.story.domain.model.StoryError
import app.roleparty.campaign.story.test.StoryBehaviorTest
import app.roleparty.campaign.story.test.document
import app.roleparty.core.document.Document
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe

class SceneChangingServiceTest : StoryBehaviorTest() {

    init {
        Given("a scene") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            val sceneId = fixtures.`given a scene`(campaignId)

            When("the game master renames it") {
                fixtures.changeScene(gameMaster, campaignId, sceneId, "Night ambush", document("In the dark.")).shouldBeSuccess()

                Then("it should hold the new name and description") {
                    fixtures.scene(sceneId)?.name shouldBe "Night ambush"
                    fixtures.scene(sceneId)?.description shouldBe document("In the dark.")
                }
            }

            Then("an unknown scene or a blank name should be refused") {
                val unknown = SceneId.generate()
                fixtures.changeScene(gameMaster, campaignId, unknown, "A", Document.EMPTY) shouldBeFailure StoryError.SceneNotFound(unknown)
                fixtures.changeScene(gameMaster, campaignId, sceneId, " ", Document.EMPTY) shouldBeFailure StoryError.BlankName
            }
        }
    }
}
