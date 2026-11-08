package app.roleparty.campaign.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.test.CampaignBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.media.commons.ImageId
import io.kotest.matchers.shouldBe
import app.roleparty.campaign.domain.model.playercharacter.classes.BerserkerBarbarian3
import app.roleparty.campaign.domain.model.playercharacter.classes.CharacterClass
import io.kotest.matchers.types.shouldBeInstanceOf

class PlayerCharacterLevelingUpServiceTest : CampaignBehaviorTest() {

    private val levelUp = fixtures.levelUpPlayerCharacter

    init {
        Given("a level 1 barbarian") {
            val campaignId = fixtures.`given a campaign`()
            val player = fixtures.`given a player of`(campaignId)
            val id = fixtures.`given a player character`(campaignId, player, CharacterClass.BARBARIAN)

            When("its player levels it up twice") {
                levelUp(player, id, CharacterClass.BARBARIAN).shouldBeSuccess()
                levelUp(player, id, CharacterClass.BARBARIAN).shouldBeSuccess()

                Then("it should reach level 3 with its subclass") {
                    val character = fixtures.playerCharacter(id)
                    character.level shouldBe 3
                    character.classes.single().shouldBeInstanceOf<BerserkerBarbarian3>()
                }
            }

            When("leveling up a class it does not hold") {
                Then("it should be refused") {
                    levelUp(player, id, CharacterClass.WIZARD) shouldBeFailure CampaignError.ClassNotHeld(CharacterClass.WIZARD)
                }
            }

            When("someone else levels it up") {
                Then("it should be refused") {
                    levelUp(AccountId.generate(), id, CharacterClass.BARBARIAN) shouldBeFailure CampaignError.PlayerCharacterOwnerRequired
                }
            }

        }

        Given("a barbarian of level 20") {
            val campaignId = fixtures.`given a campaign`()
            val player = fixtures.`given a player of`(campaignId)
            val id = fixtures.`given a player character`(campaignId, player, CharacterClass.BARBARIAN)

            When("leveling it up again") {
                repeat(19) { levelUp(player, id, CharacterClass.BARBARIAN).shouldBeSuccess() }

                Then("it should not go further") {
                    fixtures.playerCharacter(id).level shouldBe 20
                    levelUp(player, id, CharacterClass.BARBARIAN) shouldBeFailure CampaignError.MaxLevelReached(CharacterClass.BARBARIAN)
                }
            }
        }
    }
}
