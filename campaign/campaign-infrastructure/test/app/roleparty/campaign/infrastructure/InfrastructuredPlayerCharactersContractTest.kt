package app.roleparty.campaign.infrastructure

import app.roleparty.campaign.test.PlayerCharactersContractTest

class InfrastructuredPlayerCharactersContractTest : PlayerCharactersContractTest({ TestPostgres.stores() })
