package app.roleparty.campaign.tabletop.test

import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.tabletop.domain.model.PlayerCharacter
import app.roleparty.campaign.tabletop.domain.port.secondary.PlayerCharacters
import app.roleparty.core.test.FixturedBehaviorTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class PlayerCharactersContractSubject(val playerCharacters: PlayerCharacters, val givenPlayerCharacter: suspend (String) -> PlayerCharacter)

abstract class PlayerCharactersContractTest(createSubject: () -> PlayerCharactersContractSubject) : FixturedBehaviorTest() {

    private val subject = createSubject()

    init {
        Given("a player character") {
            val gimli = subject.givenPlayerCharacter("Gimli")

            Then("it should be found with its campaign, player, name and image") {
                subject.playerCharacters.findById(gimli.id) shouldBe gimli
                gimli.name shouldBe "Gimli"
            }

            Then("an unknown player character should not be found") {
                subject.playerCharacters.findById(PlayerCharacterId.generate()).shouldBeNull()
            }
        }
    }
}
