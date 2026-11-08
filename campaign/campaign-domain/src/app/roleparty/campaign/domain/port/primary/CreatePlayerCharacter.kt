package app.roleparty.campaign.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.PlayerCharacterId
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.domain.model.playercharacter.PlayerCharacterCreation
import app.roleparty.core.commons.Outcome

interface CreatePlayerCharacter {

    suspend operator fun invoke(player: AccountId, creation: PlayerCharacterCreation): Outcome<PlayerCharacterId, CampaignError>
}
