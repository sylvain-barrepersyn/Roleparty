package app.roleparty.campaign.creature.domain.port.secondary

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.domain.model.MonsterSheet
import app.roleparty.campaign.creature.domain.model.MonsterSheetOverview

interface MonsterSheets {

    suspend fun save(monsterSheet: MonsterSheet)

    suspend fun findById(monsterSheetId: MonsterSheetId): MonsterSheet?

    suspend fun overviewsOf(campaignId: CampaignId): List<MonsterSheetOverview>
}
