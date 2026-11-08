package app.roleparty.campaign.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.test.CampaignBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.media.commons.ImageId
import io.kotest.matchers.shouldBe
import app.roleparty.campaign.domain.model.playercharacter.SavingThrowProficiency
import app.roleparty.campaign.domain.model.playercharacter.SkillProficiency
import app.roleparty.campaign.domain.model.playercharacter.classes.CharacterClass
import app.roleparty.campaign.test.CampaignSamples

class PlayerCharacterCreatingServiceTest : CampaignBehaviorTest() {

    init {
        Given("a player of a campaign") {
            val campaignId = fixtures.`given a campaign`()
            val player = fixtures.`given a player of`(campaignId)

            When("creating a level 1 rogue with four skills") {
                val id = fixtures.createPlayerCharacter(player, CampaignSamples.creation(campaignId, CharacterClass.ROGUE)).shouldBeSuccess()

                Then("the character should hold its choices and the derived rules") {
                    val character = fixtures.playerCharacter(id)
                    character.player shouldBe player
                    character.level shouldBe 1
                    character.levelOf(CharacterClass.ROGUE) shouldBe 1
                    character.savingThrows shouldBe setOf(SavingThrowProficiency.DexteritySave, SavingThrowProficiency.IntelligenceSave)
                    character.skillProficiencies shouldBe CampaignSamples.SKILLS.take(4).toSet() + setOf(SkillProficiency.Investigation, SkillProficiency.Persuasion)
                    character.proficiencyBonus shouldBe 2
                }
            }

            When("creating a fighter with three skills instead of two") {
                val creation = CampaignSamples.creation(campaignId).copy(skillProficiencies = CampaignSamples.SKILLS.take(3).toSet())

                Then("it should be refused") {
                    fixtures.createPlayerCharacter(player, creation) shouldBeFailure CampaignError.WrongSkillCount(2)
                }
            }

            When("creating a character with a blank name") {
                Then("it should be refused") {
                    fixtures.createPlayerCharacter(player, CampaignSamples.creation(campaignId).copy(name = "")) shouldBeFailure CampaignError.BlankName
                }
            }
        }

        Given("someone outside a campaign") {
            val campaignId = fixtures.`given a campaign`()

            Then("creating a character in it should be refused") {
                fixtures.createPlayerCharacter(AccountId.generate(), CampaignSamples.creation(campaignId)) shouldBeFailure CampaignError.ParticipantRequired
            }
        }
    }
}
