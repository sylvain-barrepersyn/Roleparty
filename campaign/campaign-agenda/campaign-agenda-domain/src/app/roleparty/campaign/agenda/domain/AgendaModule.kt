package app.roleparty.campaign.agenda.domain

import app.roleparty.campaign.agenda.domain.port.primary.FindGameSessions
import app.roleparty.campaign.agenda.domain.port.primary.RenameGameSession
import app.roleparty.campaign.agenda.domain.port.primary.ScheduleGameSession
import app.roleparty.campaign.agenda.domain.port.secondary.Campaigns
import app.roleparty.campaign.agenda.domain.port.secondary.GameSessions
import app.roleparty.campaign.agenda.domain.service.CampaignAccessCheckingService
import app.roleparty.campaign.agenda.domain.service.GameSessionFindingService
import app.roleparty.campaign.agenda.domain.service.GameSessionRenamingService
import app.roleparty.campaign.agenda.domain.service.GameSessionSchedulingService
import kotlin.time.Clock
import kotlinx.datetime.TimeZone

class AgendaModule(
    gameSessions: GameSessions,
    campaigns: Campaigns,
    clock: Clock = Clock.System,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
) {

    private val access = CampaignAccessCheckingService(campaigns)

    val scheduleGameSession: ScheduleGameSession = GameSessionSchedulingService(gameSessions, access, clock, timeZone)

    val renameGameSession: RenameGameSession = GameSessionRenamingService(gameSessions, access)

    val findGameSessions: FindGameSessions = GameSessionFindingService(gameSessions, access)
}
