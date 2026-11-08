package app.roleparty.campaign.agenda.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.domain.port.primary.RenameGameSession
import app.roleparty.campaign.agenda.domain.port.secondary.GameSessions
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.commons.Outcome

internal class GameSessionRenamingService(
    private val gameSessions: GameSessions,
    private val access: CampaignAccessCheckingService,
) : RenameGameSession {

    override suspend fun invoke(requester: AccountId, campaignId: CampaignId, gameSessionId: GameSessionId, name: String): Outcome<Unit, AgendaError> {
        access.asGameMaster(requester, campaignId)?.let { return Outcome.Failure(it) }

        val gameSession = gameSessions.findById(gameSessionId)?.takeIf { it.campaignId == campaignId }
            ?: return Outcome.Failure(AgendaError.GameSessionNotFound(gameSessionId))
        if (name.isBlank()) return Outcome.Failure(AgendaError.BlankName)

        gameSessions.save(gameSession.copy(name = name.trim()))
        return Outcome.success()
    }
}
