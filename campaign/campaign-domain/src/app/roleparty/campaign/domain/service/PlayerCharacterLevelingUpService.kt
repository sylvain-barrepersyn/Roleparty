package app.roleparty.campaign.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.PlayerCharacterId
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.domain.model.playercharacter.PlayerCharacter
import app.roleparty.campaign.domain.model.playercharacter.classes.CharacterClass
import app.roleparty.campaign.domain.port.primary.LevelUpPlayerCharacter
import app.roleparty.campaign.domain.port.secondary.PlayerCharacters
import app.roleparty.core.commons.Outcome

internal class PlayerCharacterLevelingUpService(private val playerCharacters: PlayerCharacters) : LevelUpPlayerCharacter {

    override suspend fun invoke(requester: AccountId, playerCharacterId: PlayerCharacterId, characterClass: CharacterClass): Outcome<PlayerCharacter, CampaignError> {
        val playerCharacter = playerCharacters.findById(playerCharacterId)
            ?: return Outcome.Failure(CampaignError.PlayerCharacterNotFound(playerCharacterId))
        if (playerCharacter.player != requester) return Outcome.Failure(CampaignError.PlayerCharacterOwnerRequired)

        val level = playerCharacter.levelOf(characterClass) ?: return Outcome.Failure(CampaignError.ClassNotHeld(characterClass))
        val next = characterClass.atLevel(level + 1) ?: return Outcome.Failure(CampaignError.MaxLevelReached(characterClass))

        val leveledUp = playerCharacter.levelUp(characterClass, next)
        playerCharacters.save(leveledUp)
        return Outcome.Success(leveledUp)
    }
}
