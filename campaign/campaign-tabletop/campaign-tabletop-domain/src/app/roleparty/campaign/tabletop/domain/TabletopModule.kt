package app.roleparty.campaign.tabletop.domain

import app.roleparty.campaign.tabletop.domain.port.primary.FindStartedGameSession
import app.roleparty.campaign.tabletop.domain.port.primary.StartGameSession
import app.roleparty.campaign.tabletop.domain.port.primary.TakeSeat
import app.roleparty.campaign.tabletop.domain.port.secondary.Campaigns
import app.roleparty.campaign.tabletop.domain.port.secondary.GameSessions
import app.roleparty.campaign.tabletop.domain.port.secondary.PlayerCharacters
import app.roleparty.campaign.tabletop.domain.port.secondary.Tabletops
import app.roleparty.campaign.tabletop.domain.service.CampaignAccessCheckingService
import app.roleparty.campaign.tabletop.domain.service.GameSessionStartingService
import app.roleparty.campaign.tabletop.domain.service.SeatTakingService
import app.roleparty.campaign.tabletop.domain.service.StartedGameSessionFindingService
import kotlin.time.Clock

class TabletopModule(
    tabletops: Tabletops,
    gameSessions: GameSessions,
    campaigns: Campaigns,
    playerCharacters: PlayerCharacters,
    clock: Clock = Clock.System,
) {

    private val access = CampaignAccessCheckingService(campaigns)

    val startGameSession: StartGameSession = GameSessionStartingService(tabletops, gameSessions, access, clock)

    val findStartedGameSession: FindStartedGameSession = StartedGameSessionFindingService(tabletops, gameSessions, access)

    val takeSeat: TakeSeat = SeatTakingService(tabletops, playerCharacters, access)
}
