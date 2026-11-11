package app.roleparty.campaign.agenda.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.model.Agenda
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.commons.Outcome

interface FindAgenda {

    suspend fun ofCampaign(requester: AccountId, campaignId: CampaignId): Outcome<Agenda, AgendaError>
}
