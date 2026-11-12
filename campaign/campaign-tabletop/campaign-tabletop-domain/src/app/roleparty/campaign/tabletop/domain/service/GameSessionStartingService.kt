package app.roleparty.campaign.tabletop.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.tabletop.domain.model.StartedGameSession
import app.roleparty.campaign.tabletop.domain.model.Tabletop
import app.roleparty.campaign.tabletop.domain.model.TabletopError
import app.roleparty.campaign.tabletop.domain.port.primary.StartGameSession
import app.roleparty.campaign.tabletop.domain.port.secondary.GameSessions
import app.roleparty.campaign.tabletop.domain.port.secondary.Tabletops
import app.roleparty.core.commons.Outcome
import kotlin.time.Clock

internal class GameSessionStartingService(
    private val tabletops: Tabletops,
    private val gameSessions: GameSessions,
    private val access: CampaignAccessCheckingService,
    private val clock: Clock,
) : StartGameSession {

    override suspend fun invoke(requester: AccountId, campaignId: CampaignId, gameSessionId: GameSessionId): Outcome<StartedGameSession, TabletopError> {
        access.asGameMaster(requester, campaignId)?.let { return Outcome.Failure(it) }

        val gameSession = gameSessions.findById(gameSessionId)?.takeIf { it.campaignId == campaignId }
            ?: return Outcome.Failure(TabletopError.GameSessionNotFound(gameSessionId))
        if (tabletops.ofCampaign(campaignId)?.gameSessionId == gameSessionId) return Outcome.Failure(TabletopError.GameSessionAlreadyStarted)

        val tabletop = Tabletop(campaignId, gameSessionId, clock.now())
        tabletops.save(tabletop)
        return Outcome.Success(StartedGameSession(gameSession, tabletop.startedAt))
    }
}
