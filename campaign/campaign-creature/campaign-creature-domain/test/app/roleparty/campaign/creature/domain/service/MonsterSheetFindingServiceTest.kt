package app.roleparty.campaign.creature.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.test.CreatureBehaviorTest
import app.roleparty.core.test.shouldBeFailure
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.shouldBe
import app.roleparty.campaign.creature.commons.MonsterSheetId

class MonsterSheetFindingServiceTest : CreatureBehaviorTest() {

    init {
        Given("a campaign with two monster sheets") {
            val gameMaster = AccountId.generate()
            val campaignId = fixtures.`given a campaign`(gameMaster)
            val first = fixtures.`given a monster sheet`(gameMaster, campaignId)
            val second = fixtures.`given a monster sheet`(gameMaster, campaignId)

            Then("the game master should list and read them") {
                fixtures.findMonsterSheets.ofCampaign(gameMaster, campaignId).shouldBeSuccess().map { it.id } shouldBe listOf(first, second)
                fixtures.findMonsterSheets.byId(gameMaster, campaignId, first).shouldBeSuccess().id shouldBe first
            }

            Then("they should stay private to the game master") {
                val player = AccountId.generate()
                fixtures.findMonsterSheets.ofCampaign(player, campaignId) shouldBeFailure CreatureError.GameMasterRequired
                fixtures.findMonsterSheets.byId(player, campaignId, first) shouldBeFailure CreatureError.GameMasterRequired
            }

            Then("an unknown sheet should not be found") {
                val unknown = MonsterSheetId.generate()
                fixtures.findMonsterSheets.byId(gameMaster, campaignId, unknown) shouldBeFailure CreatureError.MonsterSheetNotFound(unknown)
            }
        }
    }
}
