package app.roleparty.campaign.tabletop.infrastructure

import app.roleparty.campaign.tabletop.infrastructure.adapter.secondary.InfrastructuredPlayerCharacters
import app.roleparty.campaign.tabletop.test.PlayerCharactersContractSubject
import app.roleparty.campaign.tabletop.test.PlayerCharactersContractTest

class InfrastructuredPlayerCharactersContractTest : PlayerCharactersContractTest({
    PlayerCharactersContractSubject(InfrastructuredPlayerCharacters(TestPostgres.database)) { TestPostgres.givenPlayerCharacter(it) }
})
