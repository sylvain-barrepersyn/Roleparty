package app.roleparty.campaign.agenda.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.test.AgendaBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe
import kotlin.time.Instant
import kotlinx.datetime.LocalDate

class GameSessionRenamingServiceTest : AgendaBehaviorTest() {

    init {
        clock.setTo(Instant.parse("2026-11-07T12:00:00Z"))

        Given("a scheduled session") {
            val gameMaster = AccountId.generate()
            val player = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster, setOf(player))
            val id = fixtures.`given a game session`(gameMaster, campaignId, LocalDate(2026, 12, 1))

            When("the game master names it") {
                fixtures.renameGameSession(gameMaster, campaignId, id, " Session zero ").shouldBeSuccess()

                Then("it should hold the name") {
                    fixtures.gameSession(id).name shouldBe "Session zero"
                }
            }

            Then("a player, a blank name or another campaign should be refused") {
                fixtures.renameGameSession(player, campaignId, id, "Mine") shouldBeFailure AgendaError.GameMasterRequired
                fixtures.renameGameSession(gameMaster, campaignId, id, " ") shouldBeFailure AgendaError.BlankName
                val otherCampaign = fixtures.`given a campaign`(gameMaster)
                fixtures.renameGameSession(gameMaster, otherCampaign, id, "Other") shouldBeFailure AgendaError.GameSessionNotFound(id)
            }
        }
    }
}
