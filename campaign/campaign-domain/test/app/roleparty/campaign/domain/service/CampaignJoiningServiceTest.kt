package app.roleparty.campaign.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.test.CampaignBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.media.commons.ImageId
import io.kotest.matchers.shouldBe
import app.roleparty.campaign.domain.model.Invitation

class CampaignJoiningServiceTest : CampaignBehaviorTest() {

    init {
        Given("an invitation code") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            val code = fixtures.inviteToCampaign.generateCode(gameMaster, campaignId).shouldBeSuccess()
            val player = AccountId.generate()

            When("a player joins with it, typed in lower case") {
                val outcome = fixtures.joinCampaign(player, Invitation.Code.from(code.toString().lowercase()))

                Then("the player should join the campaign") {
                    outcome.shouldBeSuccess() shouldBe campaignId
                    fixtures.campaign(campaignId).players shouldBe setOf(player)
                }

                Then("the code should not be usable again") {
                    fixtures.joinCampaign(AccountId.generate(), code) shouldBeFailure CampaignError.InvitationCodeAlreadyUsed
                }
            }

        }

        Given("an invitation code of a game master") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            val code = fixtures.inviteToCampaign.generateCode(gameMaster, campaignId).shouldBeSuccess()

            When("the game master joins with it") {
                val outcome = fixtures.joinCampaign(gameMaster, code)

                Then("it should be refused, and the code kept") {
                    outcome shouldBeFailure CampaignError.AlreadyParticipant
                    fixtures.joinCampaign(AccountId.generate(), code).shouldBeSuccess()
                }
            }
        }

        Given("an unknown code") {
            Then("joining should be refused") {
                fixtures.joinCampaign(AccountId.generate(), Invitation.Code.generate()) shouldBeFailure CampaignError.InvalidInvitationCode
            }
        }
    }
}
