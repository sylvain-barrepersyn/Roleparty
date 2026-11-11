package app.roleparty.campaign.creature.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.domain.model.CharacterStats
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.test.CreatureBehaviorTest
import app.roleparty.campaign.creature.test.CreatureSamples
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe

class CharacterSheetUnlinkingServiceTest : CreatureBehaviorTest() {

    init {
        Given("a character linked to a dragon") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            val dragonId = fixtures.`given a dragon sheet`(gameMaster, campaignId)
            val id = fixtures.createCharacterSheet.fromMonsterSheet(gameMaster, campaignId, dragonId).shouldBeSuccess()

            When("the game master unlinks it, then changes the dragon") {
                fixtures.unlinkCharacterSheet(gameMaster, campaignId, id).shouldBeSuccess()
                val dragon = CreatureSamples.dragon(campaignId, dragonId)
                fixtures.updateMonsterSheet(gameMaster, dragon.copy(stats = dragon.stats.copy(passivePerception = 99))).shouldBeSuccess()

                Then("it should keep a copy of the stats the dragon had when unlinked") {
                    fixtures.characterSheet(id).stats shouldBe CharacterStats.Own(CreatureSamples.dragonStats())
                }

                Then("it should not be unlinked twice") {
                    fixtures.unlinkCharacterSheet(gameMaster, campaignId, id) shouldBeFailure CreatureError.CharacterSheetNotLinked
                }
            }

            Then("someone else should be refused") {
                fixtures.unlinkCharacterSheet(AccountId.generate(), campaignId, id) shouldBeFailure CreatureError.GameMasterRequired
            }
        }
    }
}
