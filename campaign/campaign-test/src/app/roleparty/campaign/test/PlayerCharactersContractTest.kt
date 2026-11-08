package app.roleparty.campaign.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.PlayerCharacterId
import app.roleparty.campaign.domain.model.playercharacter.Alignment
import app.roleparty.campaign.domain.model.playercharacter.Background
import app.roleparty.campaign.domain.model.playercharacter.GamingSetToolProficiency
import app.roleparty.campaign.domain.model.playercharacter.RareLanguage
import app.roleparty.campaign.domain.model.playercharacter.Species
import app.roleparty.campaign.domain.model.playercharacter.classes.CharacterClass
import app.roleparty.core.test.FixturedBehaviorTest
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

abstract class PlayerCharactersContractTest(createStores: () -> CampaignStores) : FixturedBehaviorTest() {

    private val stores = createStores()
    private val playerCharacters = stores.playerCharacters

    init {
        Given("a saved player character") {
            val campaign = CampaignSamples.campaign().also { stores.campaigns.create(it) }
            val player = AccountId.generate()
            val playerCharacter = CampaignSamples.playerCharacter(campaign.id, player, CharacterClass.ROGUE)
            playerCharacters.save(playerCharacter)

            Then("it should be found with all its choices") {
                playerCharacters.findById(playerCharacter.id) shouldBe playerCharacter
                playerCharacters.findById(PlayerCharacterId.generate()).shouldBeNull()
            }

            Then("it should be listed for its player in its campaign only") {
                playerCharacters.ofPlayer(campaign.id, player) shouldBe listOf(playerCharacter)
                playerCharacters.ofPlayer(campaign.id, AccountId.generate()) shouldBe emptyList()
            }

            When("it is saved again after a level up and other changes") {
                val changed = playerCharacter.copy(
                    classes = listOfNotNull(CharacterClass.ROGUE.atLevel(3)),
                    alignment = Alignment.CHAOTIC_NEUTRAL,
                    species = Species.Goliath(Species.Goliath.Size.LARGE),
                    background = Background.Noble(GamingSetToolProficiency.DragonchessSet),
                    languages = setOf(RareLanguage.THIEVES_CANT),
                )
                playerCharacters.save(changed)

                Then("it should be found as changed") {
                    playerCharacters.findById(playerCharacter.id) shouldBe changed
                }
            }
        }
    }
}
