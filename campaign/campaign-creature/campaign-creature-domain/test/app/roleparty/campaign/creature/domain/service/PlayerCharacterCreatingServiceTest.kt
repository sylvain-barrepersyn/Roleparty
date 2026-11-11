package app.roleparty.campaign.creature.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.domain.model.playercharacter.Ability
import app.roleparty.campaign.creature.domain.model.playercharacter.CharacterClass
import app.roleparty.campaign.creature.domain.model.playercharacter.HitDie
import app.roleparty.campaign.creature.domain.model.playercharacter.Skill
import app.roleparty.campaign.creature.test.CreatureBehaviorTest
import app.roleparty.campaign.creature.test.CreatureSamples
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe

class PlayerCharacterCreatingServiceTest : CreatureBehaviorTest() {

    init {
        Given("a player of a campaign") {
            val campaignId = fixtures.`given a campaign`()
            val player = fixtures.`given a player of`(campaignId)

            When("creating a character of a class unknown to the rules") {
                val creation = CreatureSamples.playerCharacterCreation(campaignId, " Runesmith ").copy(languages = listOf("Common", " Dwarvish", "", "Common"))
                val id = fixtures.createPlayerCharacter(player, creation).shouldBeSuccess()

                Then("it should start at level 1 in that class, with the numbers derived from its choices") {
                    val character = fixtures.playerCharacter(id)
                    character.player shouldBe player
                    character.classes shouldBe listOf(CharacterClass("Runesmith", 1, HitDie.D10))
                    character.languages shouldBe listOf("Common", "Dwarvish")
                    character.level shouldBe 1
                    character.proficiencyBonus shouldBe 2
                    character.maxHitPoints shouldBe 12
                    character.initiative shouldBe 1
                    character.armorClass shouldBe 11
                    character.passivePerception shouldBe 13
                    character.savingThrowModifier(Ability.CONSTITUTION) shouldBe 4
                    character.skillModifier(Skill.STEALTH) shouldBe 1
                }
            }

            When("a player creates several characters") {
                val other = fixtures.`given a player of`(campaignId)
                fixtures.`given a player character`(campaignId, other)
                fixtures.`given a player character`(campaignId, other)

                Then("they should all be kept") {
                    fixtures.findPlayerCharacters.ofRequester(other, campaignId).shouldBeSuccess().size shouldBe 2
                }
            }

            Then("blank names should be refused") {
                fixtures.createPlayerCharacter(player, CreatureSamples.playerCharacterCreation(campaignId).copy(name = " ")) shouldBeFailure CreatureError.BlankName
                fixtures.createPlayerCharacter(player, CreatureSamples.playerCharacterCreation(campaignId, "")) shouldBeFailure CreatureError.BlankClassName
                val creation = CreatureSamples.playerCharacterCreation(campaignId)
                fixtures.createPlayerCharacter(player, creation.copy(background = creation.background.copy(name = ""))) shouldBeFailure CreatureError.BlankBackgroundName
                fixtures.createPlayerCharacter(player, creation.copy(species = creation.species.copy(name = ""))) shouldBeFailure CreatureError.BlankSpeciesName
            }

            Then("impossible numbers should be refused") {
                val creation = CreatureSamples.playerCharacterCreation(campaignId)
                fixtures.createPlayerCharacter(player, creation.copy(abilityScores = creation.abilityScores.copy(wisdom = 21))) shouldBeFailure
                    CreatureError.AbilityScoreOutOfRange(Ability.WISDOM, 1, 20)
                fixtures.createPlayerCharacter(player, creation.copy(coins = -1)) shouldBeFailure CreatureError.NegativeValue
            }
        }

        Given("someone outside a campaign") {
            val campaignId = fixtures.`given a campaign`()

            Then("creating a character in it should be refused") {
                fixtures.createPlayerCharacter(AccountId.generate(), CreatureSamples.playerCharacterCreation(campaignId)) shouldBeFailure CreatureError.ParticipantRequired
            }
        }
    }
}
