package app.roleparty.campaign.tabletop.test

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.tabletop.domain.model.PlayerCharacter
import app.roleparty.media.commons.ImageId

class InMemoryPlayerCharactersContractTest : PlayerCharactersContractTest({
    val playerCharacters = InMemoryPlayerCharacters()
    PlayerCharactersContractSubject(playerCharacters) { name ->
        PlayerCharacter(PlayerCharacterId.generate(), CampaignId.generate(), AccountId.generate(), name, ImageId.generate()).also(playerCharacters::given)
    }
})
