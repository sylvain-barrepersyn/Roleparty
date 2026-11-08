package app.roleparty.campaign.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.PlayerCharacterId
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.domain.model.playercharacter.PlayerCharacter
import app.roleparty.campaign.domain.model.playercharacter.classes.CharacterClass
import app.roleparty.core.commons.Outcome

interface LevelUpPlayerCharacter {

    suspend operator fun invoke(requester: AccountId, playerCharacterId: PlayerCharacterId, characterClass: CharacterClass): Outcome<PlayerCharacter, CampaignError>
}
