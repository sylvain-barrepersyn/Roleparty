package app.roleparty.campaign.agenda.test

import app.roleparty.campaign.commons.CampaignId

class InMemoryPlayerCharactersContractTest : PlayerCharactersContractTest({
    val playerCharacters = InMemoryPlayerCharacters()
    PlayerCharactersContractSubject(playerCharacters, { CampaignId.generate() }) { campaignId, player, name -> playerCharacters.given(campaignId, player, name) }
})
