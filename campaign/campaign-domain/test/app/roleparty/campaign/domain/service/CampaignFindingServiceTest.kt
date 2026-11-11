package app.roleparty.campaign.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.test.CampaignBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate

class CampaignFindingServiceTest : CampaignBehaviorTest() {

    private val today = LocalDate(2026, 11, 9)

    init {
        Given("a campaign with a player") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            val player = fixtures.`given a player of`(campaignId)

            Then("it should be listed as mastered by its game master and played by its player") {
                fixtures.findCampaigns.masteredBy(gameMaster, today).map { it.id } shouldBe listOf(campaignId)
                fixtures.findCampaigns.playedBy(player, today).map { it.id } shouldBe listOf(campaignId)
                fixtures.findCampaigns.playedBy(gameMaster, today) shouldBe emptyList()
            }

            Then("its participants should find it, and nobody else") {
                fixtures.findCampaigns.byId(gameMaster, campaignId).shouldBeSuccess().id shouldBe campaignId
                fixtures.findCampaigns.byId(player, campaignId).shouldBeSuccess().id shouldBe campaignId
                fixtures.findCampaigns.byId(AccountId.generate(), campaignId) shouldBeFailure CampaignError.ParticipantRequired
            }
        }

        Given("campaigns with and without upcoming game sessions") {
            val gameMaster = AccountId.generate()
            val zombies = fixtures.`given a campaign named`("zombies", gameMaster)
            val curse = fixtures.`given a campaign named`("Curse of Strahd", gameMaster)
            val abyss = fixtures.`given a campaign named`("Out of the Abyss", gameMaster)
            val tomb = fixtures.`given a campaign named`("Tomb of Annihilation", gameMaster)
            val lostMine = fixtures.`given a campaign named`("Lost Mine", gameMaster)
            fixtures.`given a game session of`(zombies, LocalDate(2026, 11, 1))
            fixtures.`given a game session of`(curse, LocalDate(2026, 12, 1))
            fixtures.`given a game session of`(tomb, LocalDate(2026, 11, 20))
            fixtures.`given a game session of`(tomb, today)

            Then("they should come by nearest upcoming session, then by name") {
                val overviews = fixtures.findCampaigns.masteredBy(gameMaster, today)

                overviews.map { it.id } shouldBe listOf(tomb, curse, lostMine, abyss, zombies)
                overviews.map { it.nextSession } shouldBe listOf(today, LocalDate(2026, 12, 1), null, null, null)
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
