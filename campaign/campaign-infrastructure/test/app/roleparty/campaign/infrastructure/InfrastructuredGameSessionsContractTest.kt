package app.roleparty.campaign.infrastructure

import app.roleparty.campaign.test.GameSessionsContractTest

class InfrastructuredGameSessionsContractTest : GameSessionsContractTest({ TestPostgres.stores() })
