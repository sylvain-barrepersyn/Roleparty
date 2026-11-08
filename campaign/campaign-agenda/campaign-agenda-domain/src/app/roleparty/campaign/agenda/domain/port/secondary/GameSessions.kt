package app.roleparty.campaign.agenda.domain.port.secondary

import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.domain.model.GameSession
import app.roleparty.campaign.commons.CampaignId

interface GameSessions {

    suspend fun save(gameSession: GameSession)

    suspend fun findById(gameSessionId: GameSessionId): GameSession?

    suspend fun ofCampaign(campaignId: CampaignId): List<GameSession>
}
