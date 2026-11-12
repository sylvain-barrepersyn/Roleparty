package app.roleparty.campaign.tabletop.test

import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.tabletop.domain.model.GameSession

class InMemoryGameSessionsContractTest : GameSessionsContractTest({
    val gameSessions = InMemoryGameSessions()
    GameSessionsContractSubject(gameSessions) { date, name ->
        GameSession(GameSessionId.generate(), CampaignId.generate(), date, name).also(gameSessions::given)
    }
})
