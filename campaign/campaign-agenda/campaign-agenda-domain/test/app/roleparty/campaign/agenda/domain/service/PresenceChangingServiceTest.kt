package app.roleparty.campaign.agenda.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.domain.model.Presence
import app.roleparty.campaign.agenda.test.AgendaBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe
import kotlin.time.Instant
import kotlinx.datetime.LocalDate

class PresenceChangingServiceTest : AgendaBehaviorTest() {

    init {
        clock.setTo(Instant.parse("2026-11-07T12:00:00Z"))

        Given("a session scheduled with a game master and two players") {
            val gameMaster = AccountId.generate()
            val player = AccountId.generate()
            val other = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster, setOf(player, other))
            val id = fixtures.`given a game session`(gameMaster, campaignId, LocalDate(2026, 12, 1))

            When("a player answers for themselves, then changes their mind") {
                fixtures.changePresence(player, campaignId, id, player, Presence.IN_PERSON_WITH_COMPUTER).shouldBeSuccess()
                fixtures.changePresence(player, campaignId, id, player, Presence.REMOTE).shouldBeSuccess()

                Then("only their latest answer should be kept") {
                    fixtures.gameSession(id).presences[player] shouldBe Presence.REMOTE
                }
            }

            When("the game master answers for themselves and for a player") {
                fixtures.changePresence(gameMaster, campaignId, id, gameMaster, Presence.IN_PERSON_WITHOUT_COMPUTER).shouldBeSuccess()
                fixtures.changePresence(gameMaster, campaignId, id, other, Presence.IN_PERSON_WITH_COMPUTER).shouldBeSuccess()

                Then("both answers should be kept") {
                    val presences = fixtures.gameSession(id).presences
                    presences[gameMaster] shouldBe Presence.IN_PERSON_WITHOUT_COMPUTER
                    presences[other] shouldBe Presence.IN_PERSON_WITH_COMPUTER
                }
            }

            Then("a player should not answer for someone else, nor someone outside the campaign") {
                fixtures.changePresence(player, campaignId, id, other, Presence.REMOTE) shouldBeFailure AgendaError.OthersPresenceForbidden
                fixtures.changePresence(player, campaignId, id, gameMaster, Presence.REMOTE) shouldBeFailure AgendaError.OthersPresenceForbidden
                val stranger = AccountId.generate()
                fixtures.changePresence(stranger, campaignId, id, stranger, Presence.REMOTE) shouldBeFailure AgendaError.ParticipantRequired
            }

            Then("the game master should not be absent, nor answer for someone outside the campaign") {
                fixtures.changePresence(gameMaster, campaignId, id, gameMaster, Presence.ABSENT) shouldBeFailure AgendaError.GameMasterCannotBeAbsent
                val stranger = AccountId.generate()
                fixtures.changePresence(gameMaster, campaignId, id, stranger, Presence.REMOTE) shouldBeFailure AgendaError.ParticipantNotFound(stranger)
            }

            Then("an unknown session should not be found") {
                val unknown = GameSessionId.generate()
                fixtures.changePresence(player, campaignId, unknown, player, Presence.REMOTE) shouldBeFailure AgendaError.GameSessionNotFound(unknown)
            }

            When("the session is over") {
                clock.setTo(Instant.parse("2026-12-02T09:00:00Z"))

                Then("presences should not change anymore") {
                    fixtures.changePresence(player, campaignId, id, player, Presence.REMOTE) shouldBeFailure AgendaError.GameSessionInThePast
                    fixtures.changePresence(gameMaster, campaignId, id, player, Presence.REMOTE) shouldBeFailure AgendaError.GameSessionInThePast
                }
            }

            When("the session takes place today") {
                clock.setTo(Instant.parse("2026-12-01T20:00:00Z"))

                Then("presences should still change") {
                    fixtures.changePresence(player, campaignId, id, player, Presence.REMOTE).shouldBeSuccess()
                }
            }
        }
    }
}
