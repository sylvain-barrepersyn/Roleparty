package app.roleparty.campaign.agenda.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.test.AgendaBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe
import kotlin.time.Instant
import kotlinx.datetime.LocalDate

class GameSessionFindingServiceTest : AgendaBehaviorTest() {

    init {
        clock.setTo(Instant.parse("2026-11-07T12:00:00Z"))

        Given("a campaign with two sessions") {
            val gameMaster = AccountId.generate()
            val player = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster, setOf(player))
            val later = fixtures.`given a game session`(gameMaster, campaignId, LocalDate(2027, 1, 1))
            val sooner = fixtures.`given a game session`(gameMaster, campaignId, LocalDate(2026, 12, 1))

            Then("the game master and the players should list them by date") {
                fixtures.findGameSessions.ofCampaign(gameMaster, campaignId).shouldBeSuccess().map { it.id } shouldBe listOf(sooner, later)
                fixtures.findGameSessions.ofCampaign(player, campaignId).shouldBeSuccess().map { it.id } shouldBe listOf(sooner, later)
            }

            Then("someone outside the campaign should not") {
                fixtures.findGameSessions.ofCampaign(AccountId.generate(), campaignId) shouldBeFailure AgendaError.ParticipantRequired
            }
        }
    }
}
