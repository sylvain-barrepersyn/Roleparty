package app.roleparty.campaign.agenda.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.model.AgendaError
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
    }
}
