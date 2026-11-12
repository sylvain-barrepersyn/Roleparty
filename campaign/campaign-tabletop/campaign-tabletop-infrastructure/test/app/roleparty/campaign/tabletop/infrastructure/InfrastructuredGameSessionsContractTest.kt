package app.roleparty.campaign.tabletop.infrastructure

import app.roleparty.campaign.tabletop.infrastructure.adapter.secondary.InfrastructuredGameSessions
import app.roleparty.campaign.tabletop.test.GameSessionsContractSubject
import app.roleparty.campaign.tabletop.test.GameSessionsContractTest

class InfrastructuredGameSessionsContractTest : GameSessionsContractTest({
    GameSessionsContractSubject(InfrastructuredGameSessions(TestPostgres.database)) { date, name ->
        TestPostgres.givenGameSession(TestPostgres.givenCampaign(), date, name)
    }
})
