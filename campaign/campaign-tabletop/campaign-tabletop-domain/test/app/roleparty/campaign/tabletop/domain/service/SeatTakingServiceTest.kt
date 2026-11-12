package app.roleparty.campaign.tabletop.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.tabletop.domain.model.Occupant
import app.roleparty.campaign.tabletop.domain.model.Seat
import app.roleparty.campaign.tabletop.domain.model.TabletopError
import app.roleparty.campaign.tabletop.test.TabletopBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe

class SeatTakingServiceTest : TabletopBehaviorTest() {

    init {
        Given("a campaign whose game session was started") {
            val gameMaster = AccountId.generate()
            val player = AccountId.generate()
            val other = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster, setOf(player, other))
            val gimli = fixtures.`given a player character`(campaignId, player, "Gimli")
            fixtures.`given a started game session`(gameMaster, campaignId)

            Then("the game master should sit as the game master") {
                fixtures.takeSeat(gameMaster, campaignId, null).shouldBeSuccess() shouldBe Seat(gameMaster, Occupant.GameMaster)
            }

            Then("a player should sit with one of their characters") {
                fixtures.takeSeat(player, campaignId, gimli.id).shouldBeSuccess() shouldBe Seat(player, Occupant.Player(gimli.id, "Gimli", gimli.imageId))
            }

            Then("a player without a character, with someone else's or with one of another campaign should be refused") {
                fixtures.takeSeat(player, campaignId, null) shouldBeFailure TabletopError.PlayerCharacterRequired
                fixtures.takeSeat(other, campaignId, gimli.id) shouldBeFailure TabletopError.PlayerCharacterNotFound(gimli.id)
                val elsewhere = fixtures.`given a player character`(fixtures.`given a campaign`(gameMaster, setOf(player)), player)
                fixtures.takeSeat(player, campaignId, elsewhere.id) shouldBeFailure TabletopError.PlayerCharacterNotFound(elsewhere.id)
                val unknown = PlayerCharacterId.generate()
                fixtures.takeSeat(player, campaignId, unknown) shouldBeFailure TabletopError.PlayerCharacterNotFound(unknown)
            }

            Then("a stranger should be refused") {
                fixtures.takeSeat(AccountId.generate(), campaignId, null) shouldBeFailure TabletopError.ParticipantRequired
            }
        }

        Given("a campaign where no game session was started") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)

            Then("nobody should sit") {
                fixtures.takeSeat(gameMaster, campaignId, null) shouldBeFailure TabletopError.GameSessionNotStarted
            }
        }
    }
}
