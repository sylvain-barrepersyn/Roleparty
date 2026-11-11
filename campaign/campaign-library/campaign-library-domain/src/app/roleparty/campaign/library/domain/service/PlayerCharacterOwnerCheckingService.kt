package app.roleparty.campaign.library.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.campaign.library.domain.port.secondary.PlayerCharacters

internal class PlayerCharacterOwnerCheckingService(private val playerCharacters: PlayerCharacters) {

    suspend fun check(requester: AccountId, playerCharacterId: PlayerCharacterId): LibraryError? {
        val player = playerCharacters.playerOf(playerCharacterId) ?: return LibraryError.PlayerCharacterNotFound(playerCharacterId)
        return if (player == requester) null else LibraryError.PlayerCharacterOwnerRequired
    }
}
