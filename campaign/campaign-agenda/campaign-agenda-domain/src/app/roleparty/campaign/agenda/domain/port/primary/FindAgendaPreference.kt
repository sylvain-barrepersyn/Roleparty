package app.roleparty.campaign.agenda.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.domain.model.ParticipantAgendaPreference
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.commons.Outcome

interface FindAgendaPreference {

    suspend fun ofRequester(requester: AccountId, campaignId: CampaignId): Outcome<ParticipantAgendaPreference, AgendaError>
}
