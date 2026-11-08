package app.roleparty.campaign.creature.test

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.domain.model.MonsterSheet
import app.roleparty.campaign.creature.domain.model.MonsterSheetOverview
import app.roleparty.campaign.creature.domain.port.secondary.MonsterSheets

internal class InMemoryMonsterSheets : MonsterSheets {

    private val monsterSheets = linkedMapOf<MonsterSheetId, MonsterSheet>()

    override suspend fun save(monsterSheet: MonsterSheet) {
        monsterSheets[monsterSheet.id] = monsterSheet
    }

    override suspend fun findById(monsterSheetId: MonsterSheetId) = monsterSheets[monsterSheetId]

    override suspend fun overviewsOf(campaignId: CampaignId) = monsterSheets.values
        .filter { it.campaignId == campaignId }
        .map { MonsterSheetOverview(it.id, it.name, it.image, it.type, it.size, it.challengeRating) }
}
