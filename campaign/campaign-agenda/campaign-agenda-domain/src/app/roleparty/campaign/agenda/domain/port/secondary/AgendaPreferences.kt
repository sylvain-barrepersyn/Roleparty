package app.roleparty.campaign.agenda.domain.port.secondary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.model.AgendaPreference
import app.roleparty.campaign.commons.CampaignId

interface AgendaPreferences {

    suspend fun save(campaignId: CampaignId, accountId: AccountId, preference: AgendaPreference)

    suspend fun ofCampaign(campaignId: CampaignId): Map<AccountId, AgendaPreference>
}
