package app.roleparty.campaign.creature.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.domain.model.CharacterSheetOverview
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.domain.model.StatBlock
import app.roleparty.campaign.creature.test.CreatureBehaviorTest
import app.roleparty.campaign.creature.test.CreatureSamples
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe

class CharacterSheetFindingServiceTest : CreatureBehaviorTest() {

    init {
        Given("a character linked to a dragon and an empty character") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            val dragonId = fixtures.`given a dragon sheet`(gameMaster, campaignId)
            val linked = fixtures.createCharacterSheet.fromMonsterSheet(gameMaster, campaignId, dragonId).shouldBeSuccess()
            val empty = fixtures.createCharacterSheet.empty(gameMaster, campaignId).shouldBeSuccess()
            val dragon = CreatureSamples.dragonStats()

            Then("the linked character should be read with the stats of the dragon") {
                val resolved = fixtures.findCharacterSheets.byId(gameMaster, campaignId, linked).shouldBeSuccess()
                resolved.stats shouldBe dragon
                resolved.linkedTo shouldBe dragonId
            }

            Then("both should be listed with the type of their stats") {
                fixtures.findCharacterSheets.ofCampaign(gameMaster, campaignId).shouldBeSuccess() shouldBe listOf(
                    CharacterSheetOverview(linked, "Young Red Dragon", fixtures.characterSheet(linked).image, dragon.type, dragon.size, dragon.challengeRating, dragonId),
                    CharacterSheetOverview(empty, "Character", null, StatBlock.DEFAULT.type, StatBlock.DEFAULT.size, StatBlock.DEFAULT.challengeRating, null),
                )
            }

            Then("nobody else should read them") {
                fixtures.findCharacterSheets.ofCampaign(AccountId.generate(), campaignId) shouldBeFailure CreatureError.GameMasterRequired
                val otherCampaign = fixtures.`given a campaign`(gameMaster)
                fixtures.findCharacterSheets.byId(gameMaster, otherCampaign, linked) shouldBeFailure CreatureError.CharacterSheetNotFound(linked)
            }
        }
    }
}
