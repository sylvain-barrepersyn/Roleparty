package app.roleparty.campaign.creature.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.domain.model.CharacterSheetChange
import app.roleparty.campaign.creature.domain.model.CharacterStats
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.domain.model.CreatureError.InvalidSheet.Field
import app.roleparty.campaign.creature.domain.model.StatBlock
import app.roleparty.campaign.creature.test.CreatureBehaviorTest
import app.roleparty.campaign.creature.test.CreatureSamples
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.media.commons.ImageId
import io.kotest.matchers.shouldBe

class CharacterSheetUpdatingServiceTest : CreatureBehaviorTest() {

    init {
        Given("a character linked to a dragon and an empty character") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            val dragonId = fixtures.`given a dragon sheet`(gameMaster, campaignId)
            val linked = fixtures.createCharacterSheet.fromMonsterSheet(gameMaster, campaignId, dragonId).shouldBeSuccess()
            val empty = fixtures.createCharacterSheet.empty(gameMaster, campaignId).shouldBeSuccess()
            val image = ImageId.generate()

            When("the game master renames the linked character and writes notes") {
                fixtures.updateCharacterSheet(gameMaster, campaignId, linked, CharacterSheetChange(" Ember ", image, " Hoards **gold**. ", null)).shouldBeSuccess()

                Then("it should hold them, and stay linked to the dragon") {
                    val character = fixtures.characterSheet(linked)
                    character.name shouldBe "Ember"
                    character.image shouldBe image
                    character.notes shouldBe "Hoards **gold**."
                    character.stats shouldBe CharacterStats.Inherited(dragonId)
                }
            }

            Then("the stats of the linked character should be read-only") {
                val change = CharacterSheetChange("Ember", null, "", CreatureSamples.dragonStats())
                fixtures.updateCharacterSheet(gameMaster, campaignId, linked, change) shouldBeFailure CreatureError.InheritedStatsReadOnly
            }

            When("the game master changes the stats of the empty character") {
                fixtures.updateCharacterSheet(gameMaster, campaignId, empty, CharacterSheetChange("Ireena", null, "", CreatureSamples.dragonStats())).shouldBeSuccess()

                Then("it should hold them") {
                    fixtures.characterSheet(empty).stats shouldBe CharacterStats.Own(CreatureSamples.dragonStats())
                }
            }

            Then("a blank name, invalid stats or someone else should be refused") {
                fixtures.updateCharacterSheet(gameMaster, campaignId, empty, CharacterSheetChange(" ", null, "", null)) shouldBeFailure CreatureError.InvalidSheet(Field.NAME)
                val invalid = StatBlock.DEFAULT.copy(speeds = emptyMap())
                fixtures.updateCharacterSheet(gameMaster, campaignId, empty, CharacterSheetChange("Ireena", null, "", invalid)) shouldBeFailure CreatureError.InvalidSheet(Field.SPEEDS)
                fixtures.updateCharacterSheet(AccountId.generate(), campaignId, empty, CharacterSheetChange("Ireena", null, "", null)) shouldBeFailure
                    CreatureError.GameMasterRequired
            }
        }
    }
}
