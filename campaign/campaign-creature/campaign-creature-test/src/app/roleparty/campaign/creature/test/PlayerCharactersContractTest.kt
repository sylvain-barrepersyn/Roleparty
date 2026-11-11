package app.roleparty.campaign.creature.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.domain.port.secondary.PlayerCharacters
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.creature.domain.model.playercharacter.Alignment
import app.roleparty.campaign.creature.domain.model.playercharacter.CharacterClass
import app.roleparty.campaign.creature.domain.model.playercharacter.HitDie
import app.roleparty.campaign.creature.domain.model.playercharacter.Size
import app.roleparty.core.test.FixturedBehaviorTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

class PlayerCharactersContractSubject(val playerCharacters: PlayerCharacters, val givenCampaign: suspend () -> CampaignId)

abstract class PlayerCharactersContractTest(createSubject: () -> PlayerCharactersContractSubject) : FixturedBehaviorTest() {

    private val subject = createSubject()
    private val playerCharacters = subject.playerCharacters

    init {
        Given("a saved player character") {
            val campaignId = subject.givenCampaign()
            val player = AccountId.generate()
            val playerCharacter = CreatureSamples.playerCharacter(campaignId, player)
            playerCharacters.save(playerCharacter)

            Then("it should be found with all its choices") {
                playerCharacters.findById(playerCharacter.id) shouldBe playerCharacter
                playerCharacters.findById(PlayerCharacterId.generate()).shouldBeNull()
            }

            Then("it should be listed for its player in its campaign only") {
                playerCharacters.ofPlayer(campaignId, player) shouldBe listOf(playerCharacter)
                playerCharacters.ofPlayer(campaignId, AccountId.generate()) shouldBe emptyList()
            }

            When("it is saved again with other classes and changes") {
                val changed = playerCharacter.copy(
                    classes = listOf(CharacterClass("Runesmith", 3, HitDie.D10), CharacterClass("Blood hunter", 1, HitDie.D10)),
                    alignment = Alignment.CHAOTIC_NEUTRAL,
                    species = playerCharacter.species.copy(name = "Goliath", size = Size.LARGE, speed = 35),
                    languages = listOf("Common", "Thieves' Cant"),
                )
                playerCharacters.save(changed)

                Then("it should be found as changed") {
                    playerCharacters.findById(playerCharacter.id) shouldBe changed
                }
            }
        }
    }
}
