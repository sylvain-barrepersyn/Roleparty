package app.roleparty.campaign.agenda.infrastructure

import app.roleparty.campaign.agenda.infrastructure.adapter.secondary.InfrastructuredGameSessions
import app.roleparty.campaign.agenda.test.GameSessionsContractSubject
import app.roleparty.campaign.agenda.test.GameSessionsContractTest

class InfrastructuredGameSessionsContractTest : GameSessionsContractTest({
    GameSessionsContractSubject(InfrastructuredGameSessions(TestPostgres.database)) { TestPostgres.givenCampaign() }
})
