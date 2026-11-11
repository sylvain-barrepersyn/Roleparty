package app.roleparty.campaign.agenda.infrastructure

import app.roleparty.campaign.agenda.infrastructure.adapter.secondary.InfrastructuredPlayerCharacters
import app.roleparty.campaign.agenda.test.PlayerCharactersContractSubject
import app.roleparty.campaign.agenda.test.PlayerCharactersContractTest

class InfrastructuredPlayerCharactersContractTest : PlayerCharactersContractTest({
    PlayerCharactersContractSubject(InfrastructuredPlayerCharacters(TestPostgres.database), { TestPostgres.givenCampaign() }) { campaignId, player, name ->
        TestPostgres.givenPlayerCharacter(campaignId, player, name)
    }
})
