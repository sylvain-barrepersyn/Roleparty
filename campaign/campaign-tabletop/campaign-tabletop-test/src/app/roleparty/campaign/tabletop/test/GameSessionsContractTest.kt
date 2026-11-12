package app.roleparty.campaign.tabletop.test

import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.tabletop.domain.model.GameSession
import app.roleparty.campaign.tabletop.domain.port.secondary.GameSessions
import app.roleparty.core.test.FixturedBehaviorTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlinx.datetime.LocalDate

class GameSessionsContractSubject(val gameSessions: GameSessions, val givenGameSession: suspend (LocalDate, String?) -> GameSession)

abstract class GameSessionsContractTest(createSubject: () -> GameSessionsContractSubject) : FixturedBehaviorTest() {

    private val subject = createSubject()

    init {
        Given("a named game session and an unnamed one") {
            val named = subject.givenGameSession(LocalDate(2027, 1, 15), "Session zero")
            val unnamed = subject.givenGameSession(LocalDate(2027, 3, 1), null)

            Then("each should be found with its campaign, date and name") {
                subject.gameSessions.findById(named.id) shouldBe named
                subject.gameSessions.findById(unnamed.id) shouldBe unnamed
                named.name shouldBe "Session zero"
                unnamed.name.shouldBeNull()
            }

            Then("an unknown game session should not be found") {
                subject.gameSessions.findById(GameSessionId.generate()).shouldBeNull()
            }
        }
    }
}
