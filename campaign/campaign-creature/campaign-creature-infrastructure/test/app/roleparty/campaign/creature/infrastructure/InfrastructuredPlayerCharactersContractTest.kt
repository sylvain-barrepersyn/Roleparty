package app.roleparty.campaign.creature.infrastructure

import app.roleparty.campaign.creature.infrastructure.adapter.secondary.InfrastructuredPlayerCharacters
import app.roleparty.campaign.creature.test.PlayerCharactersContractSubject
import app.roleparty.campaign.creature.test.PlayerCharactersContractTest

class InfrastructuredPlayerCharactersContractTest : PlayerCharactersContractTest({
    PlayerCharactersContractSubject(InfrastructuredPlayerCharacters(TestPostgres.database)) { TestPostgres.givenCampaign() }
})
