package app.roleparty.campaign.creature.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.CharacterSheetId
import app.roleparty.campaign.creature.domain.model.CharacterSheetOverview
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.domain.model.ResolvedCharacterSheet
import app.roleparty.core.commons.Outcome

interface FindCharacterSheets {

    suspend fun ofCampaign(requester: AccountId, campaignId: CampaignId): Outcome<List<CharacterSheetOverview>, CreatureError>

    suspend fun byId(requester: AccountId, campaignId: CampaignId, characterSheetId: CharacterSheetId): Outcome<ResolvedCharacterSheet, CreatureError>
}
