package app.roleparty.campaign.agenda.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.domain.model.GameSession
import app.roleparty.campaign.agenda.domain.port.primary.ScheduleGameSession
import app.roleparty.campaign.agenda.domain.port.secondary.GameSessions
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.commons.Outcome
import kotlin.time.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

internal class GameSessionSchedulingService(
    private val gameSessions: GameSessions,
    private val access: CampaignAccessCheckingService,
    private val clock: Clock,
    private val timeZone: TimeZone,
) : ScheduleGameSession {

    override suspend fun invoke(requester: AccountId, campaignId: CampaignId, date: LocalDate): Outcome<GameSessionId, AgendaError> {
        access.asGameMaster(requester, campaignId)?.let { return Outcome.Failure(it) }
        if (date < clock.todayIn(timeZone)) return Outcome.Failure(AgendaError.DateInThePast)

        val gameSession = GameSession(GameSessionId.generate(), campaignId, date)
        gameSessions.save(gameSession)
        return Outcome.Success(gameSession.id)
    }
}
