package app.roleparty.campaign.agenda.domain

import app.roleparty.campaign.agenda.domain.port.primary.ChangeAgendaPreference
import app.roleparty.campaign.agenda.domain.port.primary.ChangeDigitalGamingTable
import app.roleparty.campaign.agenda.domain.port.primary.ChangePresence
import app.roleparty.campaign.agenda.domain.port.primary.FindAgenda
import app.roleparty.campaign.agenda.domain.port.primary.FindAgendaPreference
import app.roleparty.campaign.agenda.domain.port.primary.RenameGameSession
import app.roleparty.campaign.agenda.domain.port.primary.ScheduleGameSession
import app.roleparty.campaign.agenda.domain.port.secondary.AgendaPreferences
import app.roleparty.campaign.agenda.domain.port.secondary.Campaigns
import app.roleparty.campaign.agenda.domain.port.secondary.GameSessions
import app.roleparty.campaign.agenda.domain.port.secondary.PlayerCharacters
import app.roleparty.campaign.agenda.domain.service.AgendaFindingService
import app.roleparty.campaign.agenda.domain.service.AgendaPreferenceChangingService
import app.roleparty.campaign.agenda.domain.service.AgendaPreferenceFindingService
import app.roleparty.campaign.agenda.domain.service.CampaignAccessCheckingService
import app.roleparty.campaign.agenda.domain.service.DigitalGamingTableChangingService
import app.roleparty.campaign.agenda.domain.service.GameSessionRenamingService
import app.roleparty.campaign.agenda.domain.service.GameSessionSchedulingService
import app.roleparty.campaign.agenda.domain.service.PresenceChangingService
import kotlin.time.Clock
import kotlinx.datetime.TimeZone

class AgendaModule(
    gameSessions: GameSessions,
    campaigns: Campaigns,
    playerCharacters: PlayerCharacters,
    agendaPreferences: AgendaPreferences,
    clock: Clock = Clock.System,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
) {

    private val access = CampaignAccessCheckingService(campaigns)

    val scheduleGameSession: ScheduleGameSession = GameSessionSchedulingService(gameSessions, access, campaigns, agendaPreferences, clock, timeZone)

    val renameGameSession: RenameGameSession = GameSessionRenamingService(gameSessions, access)

    val findAgenda: FindAgenda = AgendaFindingService(gameSessions, campaigns, playerCharacters)

    val changePresence: ChangePresence = PresenceChangingService(gameSessions, campaigns, clock, timeZone)

    val changeDigitalGamingTable: ChangeDigitalGamingTable = DigitalGamingTableChangingService(gameSessions, access, clock, timeZone)

    val findAgendaPreference: FindAgendaPreference = AgendaPreferenceFindingService(agendaPreferences, campaigns)

    val changeAgendaPreference: ChangeAgendaPreference = AgendaPreferenceChangingService(agendaPreferences, campaigns)
}
