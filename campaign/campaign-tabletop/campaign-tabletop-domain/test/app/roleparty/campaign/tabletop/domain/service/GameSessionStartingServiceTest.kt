package app.roleparty.campaign.tabletop.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.tabletop.domain.model.StartedGameSession
import app.roleparty.campaign.tabletop.domain.model.TabletopError
import app.roleparty.campaign.tabletop.test.TabletopBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe
import kotlin.time.Instant

class GameSessionStartingServiceTest : TabletopBehaviorTest() {

    init {
        clock.setTo(Instant.parse("2026-11-12T19:00:00Z"))

        Given("a campaign with a scheduled game session") {
            val gameMaster = AccountId.generate()
            val player = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster, setOf(player))
            val gameSession = fixtures.`given a game session`(campaignId, name = "Into the mists")

            When("the game master starts it") {
                val started = fixtures.startGameSession(gameMaster, campaignId, gameSession.id).shouldBeSuccess()

                Then("it should be the started game session of the campaign, for every participant") {
                    started shouldBe StartedGameSession(gameSession, Instant.parse("2026-11-12T19:00:00Z"))
                    fixtures.findStartedGameSession.ofCampaign(player, campaignId).shouldBeSuccess() shouldBe started
                }

                Then("starting it again should be refused") {
                    fixtures.startGameSession(gameMaster, campaignId, gameSession.id) shouldBeFailure TabletopError.GameSessionAlreadyStarted
                }

                And("the game master starts another game session") {
                    val next = fixtures.`given a game session`(campaignId)
                    fixtures.startGameSession(gameMaster, campaignId, next.id).shouldBeSuccess()

                    Then("the campaign should be on the latest one, and the first one can be started again") {
                        fixtures.findStartedGameSession.ofCampaign(gameMaster, campaignId).shouldBeSuccess()?.gameSession shouldBe next
                        fixtures.startGameSession(gameMaster, campaignId, gameSession.id).shouldBeSuccess()
                    }
                }
            }

            Then("a player, an unknown game session or one of another campaign should be refused") {
                fixtures.startGameSession(player, campaignId, gameSession.id) shouldBeFailure TabletopError.GameMasterRequired
                val unknown = GameSessionId.generate()
                fixtures.startGameSession(gameMaster, campaignId, unknown) shouldBeFailure TabletopError.GameSessionNotFound(unknown)
                val elsewhere = fixtures.`given a game session`(fixtures.`given a campaign`(gameMaster))
                fixtures.startGameSession(gameMaster, campaignId, elsewhere.id) shouldBeFailure TabletopError.GameSessionNotFound(elsewhere.id)
            }
        }
    }
}
