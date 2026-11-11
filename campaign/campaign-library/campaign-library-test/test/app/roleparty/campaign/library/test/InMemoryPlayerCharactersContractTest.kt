package app.roleparty.campaign.library.test

import app.roleparty.campaign.creature.commons.PlayerCharacterId

class InMemoryPlayerCharactersContractTest : PlayerCharactersContractTest({
    val playerCharacters = InMemoryPlayerCharacters()
    PlayerCharactersContractSubject(playerCharacters) { player -> PlayerCharacterId.generate().also { playerCharacters.given(it, player) } }
})
