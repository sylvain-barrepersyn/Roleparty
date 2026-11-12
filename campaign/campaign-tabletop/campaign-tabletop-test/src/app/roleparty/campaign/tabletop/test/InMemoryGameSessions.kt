package app.roleparty.campaign.tabletop.test

import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.tabletop.domain.model.GameSession
import app.roleparty.campaign.tabletop.domain.port.secondary.GameSessions

internal class InMemoryGameSessions : GameSessions {

    private val gameSessions = mutableMapOf<GameSessionId, GameSession>()

    override suspend fun findById(gameSessionId: GameSessionId) = gameSessions[gameSessionId]

    fun given(gameSession: GameSession) {
        gameSessions[gameSession.id] = gameSession
    }
}
