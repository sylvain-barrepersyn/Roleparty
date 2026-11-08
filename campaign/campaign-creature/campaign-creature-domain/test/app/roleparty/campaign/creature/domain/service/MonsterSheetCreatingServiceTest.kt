package app.roleparty.campaign.creature.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.test.CreatureBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.domain.model.MonsterSheet

class MonsterSheetCreatingServiceTest : CreatureBehaviorTest() {

    init {
        Given("a campaign") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)

            When("its game master creates a monster sheet") {
                val id = fixtures.createMonsterSheet(gameMaster, campaignId).shouldBeSuccess()

                Then("an empty sheet should be created in the campaign") {
                    fixtures.monsterSheet(id) shouldBe MonsterSheet.empty(id, campaignId)
                }
            }

            Then("anyone else should be refused") {
                fixtures.createMonsterSheet(AccountId.generate(), campaignId) shouldBeFailure CreatureError.GameMasterRequired
            }
        }

        Given("an unknown campaign") {
            val campaignId = CampaignId.generate()

            Then("creating a sheet should be refused") {
                fixtures.createMonsterSheet(AccountId.generate(), campaignId) shouldBeFailure CreatureError.CampaignNotFound(campaignId)
            }
        }
    }
}
