package app.roleparty.campaign.agenda.test

import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.domain.model.GameSession
import app.roleparty.campaign.agenda.domain.port.secondary.GameSessions
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.test.FixturedBehaviorTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate

class GameSessionsContractSubject(val gameSessions: GameSessions, val givenCampaign: suspend () -> CampaignId)

abstract class GameSessionsContractTest(createSubject: () -> GameSessionsContractSubject) : FixturedBehaviorTest() {

    private val subject = createSubject()
    private val gameSessions = subject.gameSessions

    init {
        Given("game sessions of a campaign, saved out of order") {
            val campaignId = subject.givenCampaign()
            val later = GameSession(GameSessionId.generate(), campaignId, LocalDate(2027, 3, 1))
            val sooner = GameSession(GameSessionId.generate(), campaignId, LocalDate(2027, 1, 15), "Session zero")
            gameSessions.save(later)
            gameSessions.save(sooner)

            Then("they should be found, and listed by date") {
                gameSessions.findById(sooner.id) shouldBe sooner
                gameSessions.findById(GameSessionId.generate()).shouldBeNull()
                gameSessions.ofCampaign(campaignId) shouldBe listOf(sooner, later)
                gameSessions.ofCampaign(subject.givenCampaign()) shouldBe emptyList()
            }

            When("one is renamed") {
                gameSessions.save(later.copy(name = "Finale"))

                Then("it should hold the new name") {
                    gameSessions.findById(later.id)?.name shouldBe "Finale"
                }
            }
        }
    }
}
