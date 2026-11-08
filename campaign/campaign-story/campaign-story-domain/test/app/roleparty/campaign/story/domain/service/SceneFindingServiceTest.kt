package app.roleparty.campaign.story.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.story.domain.model.StoryError
import app.roleparty.campaign.story.test.StoryBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe

class SceneFindingServiceTest : StoryBehaviorTest() {

    init {
        Given("a campaign with scenes") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            val player = AccountId.generate()
            val first = fixtures.`given a scene`(campaignId, "Ambush")
            val second = fixtures.`given a scene`(campaignId, "Tavern")

            Then("the game master should list them, and players should not") {
                fixtures.findScenes.ofCampaign(gameMaster, campaignId).shouldBeSuccess().map { it.id } shouldBe listOf(first, second)
                fixtures.findScenes.ofCampaign(player, campaignId) shouldBeFailure StoryError.GameMasterRequired
            }
        }
    }
}
