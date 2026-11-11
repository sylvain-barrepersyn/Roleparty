package app.roleparty.campaign.agenda.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.domain.model.AgendaPreference
import app.roleparty.campaign.agenda.domain.model.Presence
import app.roleparty.campaign.agenda.test.AgendaBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import app.roleparty.campaign.commons.CampaignId

class GameSessionSchedulingServiceTest : AgendaBehaviorTest() {

    init {
        clock.setTo(Instant.parse("2026-11-07T12:00:00Z"))

        Given("a campaign with a player") {
            val gameMaster = AccountId.generate()
            val player = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster, setOf(player))

            When("the game master schedules a session today") {
                val id = fixtures.scheduleGameSession(gameMaster, campaignId, LocalDate(2026, 11, 7)).shouldBeSuccess()

                Then("the session should be scheduled without a name") {
                    fixtures.gameSession(id).date shouldBe LocalDate(2026, 11, 7)
                    fixtures.gameSession(id).name shouldBe null
                }
            }

            Then("a date in the past or a player scheduling should be refused") {
                fixtures.scheduleGameSession(gameMaster, campaignId, LocalDate(2026, 11, 6)) shouldBeFailure AgendaError.DateInThePast
                fixtures.scheduleGameSession(player, campaignId, LocalDate(2026, 12, 1)) shouldBeFailure AgendaError.GameMasterRequired
            }
        }

        Given("an unknown campaign") {
            val campaignId = CampaignId.generate()

            Then("scheduling should be refused") {
                fixtures.scheduleGameSession(AccountId.generate(), campaignId, LocalDate(2026, 12, 1)) shouldBeFailure AgendaError.CampaignNotFound(campaignId)
            }
        }

        Given("participants with default presences and a game master bringing a digital gaming table") {
            val gameMaster = AccountId.generate()
            val player = AccountId.generate()
            val undecided = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster, setOf(player, undecided))
            fixtures.changeAgendaPreference(gameMaster, campaignId, AgendaPreference(Presence.IN_PERSON_WITH_COMPUTER, digitalGamingTable = true)).shouldBeSuccess()
            fixtures.changeAgendaPreference(player, campaignId, AgendaPreference(Presence.REMOTE, digitalGamingTable = false)).shouldBeSuccess()

            When("a session is scheduled, then the defaults change") {
                val id = fixtures.scheduleGameSession(gameMaster, campaignId, LocalDate(2026, 12, 1)).shouldBeSuccess()
                fixtures.changeAgendaPreference(gameMaster, campaignId, AgendaPreference(Presence.REMOTE, digitalGamingTable = false)).shouldBeSuccess()
                fixtures.changeAgendaPreference(player, campaignId, AgendaPreference(Presence.IN_PERSON_WITHOUT_COMPUTER, digitalGamingTable = false)).shouldBeSuccess()

                Then("the session should keep the defaults it was scheduled with, a participant who never chose being absent") {
                    val session = fixtures.gameSession(id)
                    session.digitalGamingTable shouldBe true
                    session.presences shouldBe mapOf(gameMaster to Presence.IN_PERSON_WITH_COMPUTER, player to Presence.REMOTE, undecided to Presence.ABSENT)
                }

                Then("a session scheduled afterwards should take the new defaults") {
                    val later = fixtures.scheduleGameSession(gameMaster, campaignId, LocalDate(2026, 12, 15)).shouldBeSuccess()
                    val session = fixtures.gameSession(later)
                    session.digitalGamingTable shouldBe false
                    session.presences shouldBe mapOf(gameMaster to Presence.REMOTE, player to Presence.IN_PERSON_WITHOUT_COMPUTER, undecided to Presence.ABSENT)
                }
            }
        }

        Given("a campaign where nobody chose a default") {
            val gameMaster = AccountId.generate()
            val player = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster, setOf(player))

            When("a session is scheduled") {
                val id = fixtures.scheduleGameSession(gameMaster, campaignId, LocalDate(2026, 12, 1)).shouldBeSuccess()

                Then("the game master should be in person with a computer, without digital gaming table, and the players absent") {
                    val session = fixtures.gameSession(id)
                    session.digitalGamingTable shouldBe false
                    session.presences shouldBe mapOf(gameMaster to Presence.IN_PERSON_WITH_COMPUTER, player to Presence.ABSENT)
                }
            }
        }
    }
}
