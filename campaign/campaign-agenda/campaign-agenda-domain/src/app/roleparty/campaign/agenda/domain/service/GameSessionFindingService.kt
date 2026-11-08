package app.roleparty.campaign.agenda.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.domain.model.GameSession
import app.roleparty.campaign.agenda.domain.port.primary.FindGameSessions
import app.roleparty.campaign.agenda.domain.port.secondary.GameSessions
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.commons.Outcome

internal class GameSessionFindingService(
    private val gameSessions: GameSessions,
    private val access: CampaignAccessCheckingService,
) : FindGameSessions {

    override suspend fun ofCampaign(requester: AccountId, campaignId: CampaignId): Outcome<List<GameSession>, AgendaError> {
        access.asParticipant(requester, campaignId)?.let { return Outcome.Failure(it) }

        return Outcome.Success(gameSessions.ofCampaign(campaignId))
    }
}
