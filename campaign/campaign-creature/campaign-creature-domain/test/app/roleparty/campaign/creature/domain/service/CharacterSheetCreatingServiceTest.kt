package app.roleparty.campaign.creature.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.domain.model.CharacterStats
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.domain.model.StatBlock
import app.roleparty.campaign.creature.test.CreatureBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe

class CharacterSheetCreatingServiceTest : CreatureBehaviorTest() {

    init {
        Given("a campaign with a dragon sheet") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            val dragonId = fixtures.`given a dragon sheet`(gameMaster, campaignId)
            val dragon = fixtures.monsterSheet(dragonId)

            When("the game master creates an empty character") {
                val id = fixtures.createCharacterSheet.empty(gameMaster, campaignId).shouldBeSuccess()

                Then("it should hold default stats of its own and no notes") {
                    val character = fixtures.characterSheet(id)
                    character.stats shouldBe CharacterStats.Own(StatBlock.DEFAULT)
                    character.notes shouldBe ""
                }
            }

            When("the game master creates a character from the dragon") {
                val id = fixtures.createCharacterSheet.fromMonsterSheet(gameMaster, campaignId, dragonId).shouldBeSuccess()

                Then("it should take the name and image of the dragon, and be linked to it") {
                    val character = fixtures.characterSheet(id)
                    character.name shouldBe dragon.name
                    character.image shouldBe dragon.image
                    character.stats shouldBe CharacterStats.Inherited(dragonId)
                }
            }

            Then("someone else, or a monster of another campaign, should be refused") {
                fixtures.createCharacterSheet.empty(AccountId.generate(), campaignId) shouldBeFailure CreatureError.GameMasterRequired
                val otherCampaign = fixtures.`given a campaign`(gameMaster)
                fixtures.createCharacterSheet.fromMonsterSheet(gameMaster, otherCampaign, dragonId) shouldBeFailure CreatureError.MonsterSheetNotFound(dragonId)
                val unknown = MonsterSheetId.generate()
                fixtures.createCharacterSheet.fromMonsterSheet(gameMaster, campaignId, unknown) shouldBeFailure CreatureError.MonsterSheetNotFound(unknown)
            }
        }
    }
}
