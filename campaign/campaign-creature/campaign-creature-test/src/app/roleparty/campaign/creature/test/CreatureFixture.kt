package app.roleparty.campaign.creature.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.domain.CreatureModule
import app.roleparty.core.test.shouldBeSuccess
import io.kotest.matchers.nulls.shouldNotBeNull

class CreatureFixture {

    private val monsterSheets = InMemoryMonsterSheets()
    private val campaigns = InMemoryCampaigns()

    val module = CreatureModule(monsterSheets, campaigns)

    val createMonsterSheet = module.createMonsterSheet
    val updateMonsterSheet = module.updateMonsterSheet
    val findMonsterSheets = module.findMonsterSheets

    fun `given a campaign`(gameMaster: AccountId = AccountId.generate()): CampaignId {
        val campaignId = CampaignId.generate()
        campaigns.given(campaignId, gameMaster)
        return campaignId
    }

    suspend fun `given a monster sheet`(gameMaster: AccountId, campaignId: CampaignId): MonsterSheetId =
        createMonsterSheet(gameMaster, campaignId).shouldBeSuccess()

    suspend fun monsterSheet(monsterSheetId: MonsterSheetId) = monsterSheets.findById(monsterSheetId).shouldNotBeNull()
}
