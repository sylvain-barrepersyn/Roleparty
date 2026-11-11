package app.roleparty.campaign.agenda.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.domain.model.AgendaPreference
import app.roleparty.campaign.agenda.domain.model.GameSession
import app.roleparty.campaign.agenda.domain.port.primary.ScheduleGameSession
import app.roleparty.campaign.agenda.domain.port.secondary.AgendaPreferences
import app.roleparty.campaign.agenda.domain.port.secondary.Campaigns
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
    private val campaigns: Campaigns,
    private val agendaPreferences: AgendaPreferences,
    private val clock: Clock,
    private val timeZone: TimeZone,
) : ScheduleGameSession {

    override suspend fun invoke(requester: AccountId, campaignId: CampaignId, date: LocalDate): Outcome<GameSessionId, AgendaError> {
        access.asGameMaster(requester, campaignId)?.let { return Outcome.Failure(it) }
        if (date < clock.todayIn(timeZone)) return Outcome.Failure(AgendaError.DateInThePast)

        val participants = campaigns.participantsOf(campaignId) ?: return Outcome.Failure(AgendaError.CampaignNotFound(campaignId))
        val stored = agendaPreferences.ofCampaign(campaignId)
        val gameMasterPreference = stored[participants.gameMaster] ?: AgendaPreference.defaultFor(gameMaster = true)
        val presences = participants.players.associateWith { (stored[it] ?: AgendaPreference.defaultFor(gameMaster = false)).presence } +
            (participants.gameMaster to gameMasterPreference.presence)
        val gameSession = GameSession(
            id = GameSessionId.generate(),
            campaignId = campaignId,
            date = date,
            digitalGamingTable = gameMasterPreference.digitalGamingTable,
            presences = presences,
        )
        gameSessions.save(gameSession)
        return Outcome.Success(gameSession.id)
    }
}
