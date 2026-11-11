package app.roleparty.campaign.agenda.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.model.Agenda
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.test.AgendaBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe
import kotlin.time.Instant
import kotlinx.datetime.LocalDate

class AgendaFindingServiceTest : AgendaBehaviorTest() {

    init {
        clock.setTo(Instant.parse("2026-11-07T12:00:00Z"))

        Given("a campaign with two sessions") {
            val gameMaster = AccountId.generate()
            val player = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster, setOf(player))
            val later = fixtures.`given a game session`(gameMaster, campaignId, LocalDate(2027, 1, 1))
            val sooner = fixtures.`given a game session`(gameMaster, campaignId, LocalDate(2026, 12, 1))

            Then("the game master and the players should list them by date") {
                fixtures.findAgenda.ofCampaign(gameMaster, campaignId).shouldBeSuccess().gameSessions.map { it.id } shouldBe listOf(sooner, later)
                fixtures.findAgenda.ofCampaign(player, campaignId).shouldBeSuccess().gameSessions.map { it.id } shouldBe listOf(sooner, later)
            }

            Then("someone outside the campaign should not") {
                fixtures.findAgenda.ofCampaign(AccountId.generate(), campaignId) shouldBeFailure AgendaError.ParticipantRequired
            }
        }

        Given("a campaign whose players have zero, one or several characters") {
            val gameMaster = AccountId.generate()
            val withTwo = AccountId.generate()
            val withOne = AccountId.generate()
            val withNone = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster, setOf(withNone, withTwo, withOne))
            fixtures.`given a player character`(campaignId, withTwo, "Gimli")
            fixtures.`given a player character`(campaignId, withTwo, "Gloin")
            fixtures.`given a player character`(campaignId, withOne, "Aragorn")

            Then("the game master should come first, then the players by character name, those without character last") {
                fixtures.findAgenda.ofCampaign(withOne, campaignId).shouldBeSuccess().participants shouldBe listOf(
                    Agenda.Participant(gameMaster, gameMaster = true, characterNames = emptyList()),
                    Agenda.Participant(withOne, gameMaster = false, characterNames = listOf("Aragorn")),
                    Agenda.Participant(withTwo, gameMaster = false, characterNames = listOf("Gimli", "Gloin")),
                    Agenda.Participant(withNone, gameMaster = false, characterNames = emptyList()),
                )
            }
        }
    }
}
