package app.roleparty.campaign.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.test.CampaignBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.media.commons.ImageId
import io.kotest.matchers.shouldBe
import app.roleparty.campaign.commons.CampaignId

class CampaignFindingServiceTest : CampaignBehaviorTest() {

    init {
        Given("a campaign with a player") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            val player = fixtures.`given a player of`(campaignId)

            Then("it should be listed as mastered by its game master and played by its player") {
                fixtures.findCampaigns.masteredBy(gameMaster).map { it.id } shouldBe listOf(campaignId)
                fixtures.findCampaigns.playedBy(player).map { it.id } shouldBe listOf(campaignId)
                fixtures.findCampaigns.playedBy(gameMaster) shouldBe emptyList()
            }

            Then("its participants should find it, and nobody else") {
                fixtures.findCampaigns.byId(gameMaster, campaignId).shouldBeSuccess().id shouldBe campaignId
                fixtures.findCampaigns.byId(player, campaignId).shouldBeSuccess().id shouldBe campaignId
                fixtures.findCampaigns.byId(AccountId.generate(), campaignId) shouldBeFailure CampaignError.ParticipantRequired
            }
        }

        Given("an unknown campaign") {
            val campaignId = CampaignId.generate()

            Then("it should not be found") {
                fixtures.findCampaigns.byId(AccountId.generate(), campaignId) shouldBeFailure CampaignError.CampaignNotFound(campaignId)
            }
        }
    }
}
