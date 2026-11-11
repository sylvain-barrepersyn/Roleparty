package app.roleparty.campaign.creature.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.CharacterSheetId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.core.commons.Outcome

interface CreateCharacterSheet {

    suspend fun empty(requester: AccountId, campaignId: CampaignId): Outcome<CharacterSheetId, CreatureError>

    suspend fun fromMonsterSheet(requester: AccountId, campaignId: CampaignId, monsterSheetId: MonsterSheetId): Outcome<CharacterSheetId, CreatureError>
}
