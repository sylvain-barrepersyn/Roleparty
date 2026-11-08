package app.roleparty.campaign.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.test.CampaignBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.media.commons.ImageId
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch

class CampaignInvitingServiceTest : CampaignBehaviorTest() {

    init {
        Given("a campaign") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)

            When("its game master generates two codes") {
                val first = fixtures.inviteToCampaign.generateCode(gameMaster, campaignId).shouldBeSuccess()
                val second = fixtures.inviteToCampaign.generateCode(gameMaster, campaignId).shouldBeSuccess()

                Then("the codes should be readable, and listed for the game master") {
                    first.toString() shouldMatch Regex("[2-9A-HJ-NP-Z]{3}(-[2-9A-HJ-NP-Z]{3}){3}")
                    fixtures.inviteToCampaign.invitations(gameMaster, campaignId).shouldBeSuccess().map { it.code }.toSet() shouldBe setOf(first, second)
                }
            }

            When("someone else generates or lists codes") {
                val other = AccountId.generate()

                Then("it should be refused") {
                    fixtures.inviteToCampaign.generateCode(other, campaignId) shouldBeFailure CampaignError.GameMasterRequired
                    fixtures.inviteToCampaign.invitations(other, campaignId) shouldBeFailure CampaignError.GameMasterRequired
                }
            }
        }
    }
}
