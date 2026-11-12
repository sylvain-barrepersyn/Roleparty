package app.roleparty.campaign.tabletop.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.tabletop.domain.model.StartedGameSession
import app.roleparty.campaign.tabletop.domain.model.TabletopError
import app.roleparty.campaign.tabletop.domain.port.primary.FindStartedGameSession
import app.roleparty.campaign.tabletop.domain.port.secondary.GameSessions
import app.roleparty.campaign.tabletop.domain.port.secondary.Tabletops
import app.roleparty.core.commons.Outcome

internal class StartedGameSessionFindingService(
    private val tabletops: Tabletops,
    private val gameSessions: GameSessions,
    private val access: CampaignAccessCheckingService,
) : FindStartedGameSession {

    override suspend fun ofCampaign(requester: AccountId, campaignId: CampaignId): Outcome<StartedGameSession?, TabletopError> {
        val participants = access.asParticipant(requester, campaignId)
        if (participants is Outcome.Failure) return participants

        val tabletop = tabletops.ofCampaign(campaignId) ?: return Outcome.Success(null)
        return Outcome.Success(gameSessions.findById(tabletop.gameSessionId)?.let { StartedGameSession(it, tabletop.startedAt) })
    }
}
