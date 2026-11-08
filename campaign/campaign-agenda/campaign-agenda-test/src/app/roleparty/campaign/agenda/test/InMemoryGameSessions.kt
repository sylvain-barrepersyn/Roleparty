package app.roleparty.campaign.agenda.test

import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.domain.model.GameSession
import app.roleparty.campaign.agenda.domain.port.secondary.GameSessions
import app.roleparty.campaign.commons.CampaignId

internal class InMemoryGameSessions : GameSessions {

    private val gameSessions = linkedMapOf<GameSessionId, GameSession>()

    override suspend fun save(gameSession: GameSession) {
        gameSessions[gameSession.id] = gameSession
    }

    override suspend fun findById(gameSessionId: GameSessionId) = gameSessions[gameSessionId]

    override suspend fun ofCampaign(campaignId: CampaignId) = gameSessions.values.filter { it.campaignId == campaignId }.sortedBy { it.date }
}
