package app.roleparty.campaign.creature.contract.web

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/v1/campaigns/{campaignId}/monster-sheets")
class MonsterSheetsResource(val campaignId: CampaignId) {

    @Serializable
    @Resource("{monsterSheetId}")
    class MonsterSheet(val parent: MonsterSheetsResource, val monsterSheetId: MonsterSheetId)
}
