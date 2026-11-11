package app.roleparty.campaign.agenda.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.test.AgendaBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe
import kotlin.time.Instant
import kotlinx.datetime.LocalDate

class DigitalGamingTableChangingServiceTest : AgendaBehaviorTest() {

    init {
        clock.setTo(Instant.parse("2026-11-07T12:00:00Z"))

        Given("a scheduled session") {
            val gameMaster = AccountId.generate()
            val player = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster, setOf(player))
            val id = fixtures.`given a game session`(gameMaster, campaignId, LocalDate(2026, 12, 1))

            Then("it should start without a digital gaming table") {
                fixtures.gameSession(id).digitalGamingTable shouldBe false
            }

            When("the game master brings a digital gaming table") {
                fixtures.changeDigitalGamingTable(gameMaster, campaignId, id, true).shouldBeSuccess()

                Then("the session should have one") {
                    fixtures.gameSession(id).digitalGamingTable shouldBe true
                }
            }

            Then("a player should not change it") {
                fixtures.changeDigitalGamingTable(player, campaignId, id, true) shouldBeFailure AgendaError.GameMasterRequired
            }

            When("the session is over") {
                clock.setTo(Instant.parse("2026-12-02T09:00:00Z"))

                Then("it should not change anymore") {
                    fixtures.changeDigitalGamingTable(gameMaster, campaignId, id, false) shouldBeFailure AgendaError.GameSessionInThePast
                }
            }
        }
    }
}
