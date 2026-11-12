package app.roleparty.campaign.tabletop.domain.port.secondary

import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.tabletop.domain.model.GameSession

interface GameSessions {

    suspend fun findById(gameSessionId: GameSessionId): GameSession?
}
