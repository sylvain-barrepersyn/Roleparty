package app.roleparty.campaign.library.infrastructure

import app.roleparty.campaign.library.infrastructure.adapter.secondary.InfrastructuredPlayerCharacters
import app.roleparty.campaign.library.test.PlayerCharactersContractSubject
import app.roleparty.campaign.library.test.PlayerCharactersContractTest

class InfrastructuredPlayerCharactersContractTest : PlayerCharactersContractTest({
    PlayerCharactersContractSubject(InfrastructuredPlayerCharacters(TestPostgres.database)) { TestPostgres.givenPlayerCharacter(it) }
})
