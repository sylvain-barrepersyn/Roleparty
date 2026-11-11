package app.roleparty.campaign.agenda.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.model.AgendaPreference
import app.roleparty.campaign.agenda.domain.port.secondary.AgendaPreferences
import app.roleparty.campaign.commons.CampaignId

internal class InMemoryAgendaPreferences : AgendaPreferences {

    private val preferences = mutableMapOf<CampaignId, Map<AccountId, AgendaPreference>>()

    override suspend fun save(campaignId: CampaignId, accountId: AccountId, preference: AgendaPreference) {
        preferences[campaignId] = preferences[campaignId].orEmpty() + (accountId to preference)
    }

    override suspend fun ofCampaign(campaignId: CampaignId) = preferences[campaignId].orEmpty()
}
