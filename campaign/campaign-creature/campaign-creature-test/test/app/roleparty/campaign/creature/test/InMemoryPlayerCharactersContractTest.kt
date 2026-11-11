package app.roleparty.campaign.creature.test

import app.roleparty.campaign.commons.CampaignId

class InMemoryPlayerCharactersContractTest : PlayerCharactersContractTest({
    PlayerCharactersContractSubject(InMemoryPlayerCharacters()) { CampaignId.generate() }
})
