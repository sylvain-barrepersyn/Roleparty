package app.roleparty.campaign.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.test.CampaignBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.media.commons.ImageId
import io.kotest.matchers.shouldBe
import io.kotest.matchers.collections.shouldBeEmpty

class CampaignCreatingServiceTest : CampaignBehaviorTest() {

    init {
        Given("a game master") {
            val gameMaster = AccountId.generate()

            When("creating a campaign") {
                val campaignId = fixtures.createCampaign(gameMaster, "  The Lost Mine ", "Goblins.", ImageId.generate()).shouldBeSuccess()

                Then("the campaign should be created with no players") {
                    val campaign = fixtures.campaign(campaignId)
                    campaign.name shouldBe "The Lost Mine"
                    campaign.gameMaster shouldBe gameMaster
                    campaign.players.shouldBeEmpty()
                }
            }

            When("creating a campaign with a blank name") {
                val outcome = fixtures.createCampaign(gameMaster, " ", "", ImageId.generate())

                Then("it should be refused") {
                    outcome shouldBeFailure CampaignError.BlankName
                }
            }
        }
    }
}
