package app.roleparty.campaign.creature.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.test.CreatureBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe
import app.roleparty.campaign.creature.domain.model.AbilityScores
import app.roleparty.campaign.creature.domain.model.CreatureSkill
import app.roleparty.campaign.creature.domain.model.SavingThrows
import app.roleparty.campaign.creature.domain.model.CreatureError.InvalidMonsterSheet.Field
import app.roleparty.campaign.creature.domain.model.LegendaryAction
import app.roleparty.campaign.creature.domain.model.LegendaryActions
import app.roleparty.campaign.creature.test.CreatureSamples
import app.roleparty.campaign.creature.domain.model.CreatureLanguage
import app.roleparty.campaign.creature.domain.model.CreatureSize
import app.roleparty.campaign.creature.domain.model.DamageType
import app.roleparty.campaign.creature.domain.model.Defenses
import app.roleparty.campaign.creature.domain.model.Languages
import app.roleparty.campaign.creature.domain.model.MonsterSheet
import app.roleparty.campaign.creature.domain.model.MovementMode
import app.roleparty.campaign.creature.domain.model.Sense
import app.roleparty.campaign.creature.domain.model.Taxonomy

class MonsterSheetUpdatingServiceTest : CreatureBehaviorTest() {

    init {
        Given("a monster sheet") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            val id = fixtures.`given a monster sheet`(gameMaster, campaignId)
            val dragon = CreatureSamples.dragon(campaignId, id)

            When("the game master saves it as a dragon") {
                fixtures.updateMonsterSheet(gameMaster, dragon).shouldBeSuccess()

                Then("it should hold the whole dragon") {
                    fixtures.monsterSheet(id) shouldBe dragon
                }
            }

            When("the game master saves untrimmed texts and languages both spoken and understood") {
                val untidy = dragon.copy(
                    taxonomy = dragon.taxonomy.copy(tags = " Chromatic "),
                    languages = Languages(setOf(CreatureLanguage.COMMON), understood = setOf(CreatureLanguage.COMMON, CreatureLanguage.ELVISH), other = " one other "),
                    gear = listOf(" Crown of embers "),
                )
                fixtures.updateMonsterSheet(gameMaster, untidy).shouldBeSuccess()

                Then("texts should be trimmed, and a spoken language not repeated as understood") {
                    val saved = fixtures.monsterSheet(id)
                    saved.taxonomy.tags shouldBe "Chromatic"
                    saved.languages shouldBe Languages(setOf(CreatureLanguage.COMMON), understood = setOf(CreatureLanguage.ELVISH), other = "one other")
                    saved.gear shouldBe listOf("Crown of embers")
                }
            }

            When("the game master saves skills without bonus") {
                fixtures.updateMonsterSheet(gameMaster, dragon.copy(skills = dragon.skills + (CreatureSkill.ARCANA to 0))).shouldBeSuccess()

                Then("only the skills with a bonus should be kept") {
                    fixtures.monsterSheet(id)?.skills shouldBe dragon.skills
                }
            }

            Then("invalid content should be refused with the faulty field") {
                fixtures.updateMonsterSheet(gameMaster, dragon.copy(name = " ")) shouldBeFailure CreatureError.InvalidMonsterSheet(Field.NAME)
                fixtures.updateMonsterSheet(gameMaster, dragon.copy(abilities = AbilityScores(31, 10, 10, 10, 10, 10))) shouldBeFailure
                    CreatureError.InvalidMonsterSheet(Field.ABILITIES)
                fixtures.updateMonsterSheet(gameMaster, dragon.copy(savingThrows = SavingThrows(wisdom = -1))) shouldBeFailure
                    CreatureError.InvalidMonsterSheet(Field.SAVING_THROWS)
                fixtures.updateMonsterSheet(gameMaster, dragon.copy(skills = mapOf(CreatureSkill.ARCANA to 21))) shouldBeFailure
                    CreatureError.InvalidMonsterSheet(Field.SKILLS)
                fixtures.updateMonsterSheet(gameMaster, dragon.copy(speeds = emptyMap())) shouldBeFailure CreatureError.InvalidMonsterSheet(Field.SPEEDS)
                fixtures.updateMonsterSheet(gameMaster, dragon.copy(legendaryActions = LegendaryActions("", listOf(LegendaryAction("Move", "", 0))))) shouldBeFailure
                    CreatureError.InvalidMonsterSheet(Field.LEGENDARY_ACTIONS)
            }

            Then("the new parts of the sheet should be checked too") {
                suspend fun refused(sheet: MonsterSheet, field: Field) =
                    fixtures.updateMonsterSheet(gameMaster, sheet) shouldBeFailure CreatureError.InvalidMonsterSheet(field)

                refused(dragon.copy(taxonomy = Taxonomy(alternativeSize = dragon.size)), Field.TAXONOMY)
                refused(dragon.copy(taxonomy = Taxonomy(swarmOf = CreatureSize.GARGANTUAN)), Field.TAXONOMY)
                refused(dragon.copy(speeds = mapOf(MovementMode.WALK to 30), hover = true), Field.SPEEDS)
                refused(dragon.copy(senses = mapOf(Sense.DARKVISION to 0)), Field.SENSES)
                refused(dragon.copy(defenses = Defenses(resistances = setOf(DamageType.FIRE), immunities = setOf(DamageType.FIRE))), Field.DEFENSES)
                refused(dragon.copy(gear = listOf(" ")), Field.GEAR)
                refused(dragon.copy(legendaryActions = dragon.legendaryActions.copy(uses = 0)), Field.LEGENDARY_ACTIONS)
                refused(dragon.copy(legendaryActions = dragon.legendaryActions.copy(uses = 3, usesInLair = 2)), Field.LEGENDARY_ACTIONS)
            }

            Then("anyone else, or the sheet moved to another campaign, should be refused") {
                fixtures.updateMonsterSheet(AccountId.generate(), dragon) shouldBeFailure CreatureError.GameMasterRequired
                val otherCampaign = fixtures.`given a campaign`(gameMaster)
                fixtures.updateMonsterSheet(gameMaster, dragon.copy(campaignId = otherCampaign)) shouldBeFailure CreatureError.MonsterSheetNotFound(id)
            }
        }
    }
}
