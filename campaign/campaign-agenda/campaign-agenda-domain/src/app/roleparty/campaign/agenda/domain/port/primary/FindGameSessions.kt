package app.roleparty.campaign.agenda.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.domain.model.GameSession
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.commons.Outcome

interface FindGameSessions {

    suspend fun ofCampaign(requester: AccountId, campaignId: CampaignId): Outcome<List<GameSession>, AgendaError>
}
