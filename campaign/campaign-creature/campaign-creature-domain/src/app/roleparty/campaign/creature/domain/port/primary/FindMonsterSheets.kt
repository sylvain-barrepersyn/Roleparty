package app.roleparty.campaign.creature.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.domain.model.MonsterSheet
import app.roleparty.campaign.creature.domain.model.MonsterSheetOverview
import app.roleparty.core.commons.Outcome

interface FindMonsterSheets {

    suspend fun ofCampaign(requester: AccountId, campaignId: CampaignId): Outcome<List<MonsterSheetOverview>, CreatureError>

    suspend fun byId(requester: AccountId, campaignId: CampaignId, monsterSheetId: MonsterSheetId): Outcome<MonsterSheet, CreatureError>
}
