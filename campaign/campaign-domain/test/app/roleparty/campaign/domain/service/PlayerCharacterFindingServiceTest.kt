package app.roleparty.campaign.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.test.CampaignBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.media.commons.ImageId
import io.kotest.matchers.shouldBe
import app.roleparty.campaign.commons.PlayerCharacterId

class PlayerCharacterFindingServiceTest : CampaignBehaviorTest() {

    init {
        Given("two players with characters in a campaign") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            val player = fixtures.`given a player of`(campaignId)
            val other = fixtures.`given a player of`(campaignId)
            val character = fixtures.`given a player character`(campaignId, player)
            fixtures.`given a player character`(campaignId, other)

            Then("each player should list only their own characters") {
                fixtures.findPlayerCharacters.ofRequester(player, campaignId).shouldBeSuccess().map { it.id } shouldBe listOf(character)
            }

            Then("the character should be readable by its player and the game master only") {
                fixtures.findPlayerCharacters.byId(player, character).shouldBeSuccess().id shouldBe character
                fixtures.findPlayerCharacters.byId(gameMaster, character).shouldBeSuccess().id shouldBe character
                fixtures.findPlayerCharacters.byId(other, character) shouldBeFailure CampaignError.PlayerCharacterOwnerRequired
            }

            Then("someone outside the campaign should not list characters") {
                fixtures.findPlayerCharacters.ofRequester(AccountId.generate(), campaignId) shouldBeFailure CampaignError.ParticipantRequired
            }
        }

        Given("an unknown character") {
            val id = PlayerCharacterId.generate()

            Then("it should not be found") {
                fixtures.findPlayerCharacters.byId(AccountId.generate(), id) shouldBeFailure CampaignError.PlayerCharacterNotFound(id)
            }
        }
    }
}
