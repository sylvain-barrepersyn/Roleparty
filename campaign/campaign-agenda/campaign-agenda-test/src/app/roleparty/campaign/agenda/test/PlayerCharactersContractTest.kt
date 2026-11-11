package app.roleparty.campaign.agenda.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.agenda.domain.port.secondary.PlayerCharacters
import app.roleparty.core.test.FixturedBehaviorTest
import io.kotest.matchers.maps.shouldBeEmpty
import io.kotest.matchers.shouldBe

class PlayerCharactersContractSubject(
    val playerCharacters: PlayerCharacters,
    val givenCampaign: suspend () -> CampaignId,
    val givenPlayerCharacter: suspend (CampaignId, AccountId, String) -> Unit,
)

abstract class PlayerCharactersContractTest(createSubject: () -> PlayerCharactersContractSubject) : FixturedBehaviorTest() {

    private val subject = createSubject()

    init {
        Given("players with characters in two campaigns") {
            val campaignId = subject.givenCampaign()
            val gimli = AccountId.generate()
            val legolas = AccountId.generate()
            subject.givenPlayerCharacter(campaignId, gimli, "Gimli")
            subject.givenPlayerCharacter(campaignId, gimli, "Gloin")
            subject.givenPlayerCharacter(campaignId, legolas, "Legolas")
            subject.givenPlayerCharacter(subject.givenCampaign(), legolas, "Elsewhere")

            Then("the names of the characters should be grouped by player, for the campaign only, in creation order") {
                subject.playerCharacters.namesByPlayer(campaignId) shouldBe mapOf(gimli to listOf("Gimli", "Gloin"), legolas to listOf("Legolas"))
                subject.playerCharacters.namesByPlayer(CampaignId.generate()).shouldBeEmpty()
            }
        }
    }
}
