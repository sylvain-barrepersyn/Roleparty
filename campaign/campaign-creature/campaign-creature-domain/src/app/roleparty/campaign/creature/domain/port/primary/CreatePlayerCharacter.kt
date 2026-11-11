package app.roleparty.campaign.creature.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.domain.model.playercharacter.PlayerCharacterCreation
import app.roleparty.core.commons.Outcome

interface CreatePlayerCharacter {

    suspend operator fun invoke(player: AccountId, creation: PlayerCharacterCreation): Outcome<PlayerCharacterId, CreatureError>
}
