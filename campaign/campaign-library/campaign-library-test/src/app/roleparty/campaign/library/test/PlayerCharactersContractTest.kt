package app.roleparty.campaign.library.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.library.domain.port.secondary.PlayerCharacters
import app.roleparty.core.test.FixturedBehaviorTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class PlayerCharactersContractSubject(val playerCharacters: PlayerCharacters, val givenPlayerCharacter: suspend (AccountId) -> PlayerCharacterId)

abstract class PlayerCharactersContractTest(createSubject: () -> PlayerCharactersContractSubject) : FixturedBehaviorTest() {

    private val subject = createSubject()

    init {
        Given("a player character") {
            val player = AccountId.generate()
            val playerCharacterId = subject.givenPlayerCharacter(player)

            Then("its player should be found, and nothing for an unknown character") {
                subject.playerCharacters.playerOf(playerCharacterId) shouldBe player
                subject.playerCharacters.playerOf(PlayerCharacterId.generate()).shouldBeNull()
            }
        }
    }
}
