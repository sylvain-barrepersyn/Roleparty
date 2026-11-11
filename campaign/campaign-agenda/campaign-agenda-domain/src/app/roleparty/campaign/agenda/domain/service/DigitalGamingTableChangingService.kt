package app.roleparty.campaign.agenda.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.domain.port.primary.ChangeDigitalGamingTable
import app.roleparty.campaign.agenda.domain.port.secondary.GameSessions
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.commons.Outcome
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

internal class DigitalGamingTableChangingService(
    private val gameSessions: GameSessions,
    private val access: CampaignAccessCheckingService,
    private val clock: Clock,
    private val timeZone: TimeZone,
) : ChangeDigitalGamingTable {

    override suspend fun invoke(requester: AccountId, campaignId: CampaignId, gameSessionId: GameSessionId, available: Boolean): Outcome<Unit, AgendaError> {
        access.asGameMaster(requester, campaignId)?.let { return Outcome.Failure(it) }

        val gameSession = gameSessions.findById(gameSessionId)?.takeIf { it.campaignId == campaignId }
            ?: return Outcome.Failure(AgendaError.GameSessionNotFound(gameSessionId))
        if (gameSession.date < clock.todayIn(timeZone)) return Outcome.Failure(AgendaError.GameSessionInThePast)

        gameSessions.save(gameSession.copy(digitalGamingTable = available))
        return Outcome.success()
    }
}
