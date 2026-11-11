package app.roleparty.campaign.creature.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.domain.model.playercharacter.PlayerCharacter
import app.roleparty.campaign.creature.domain.model.playercharacter.PlayerCharacterOverview
import app.roleparty.core.commons.Outcome

interface FindPlayerCharacters {

    suspend fun ofRequester(requester: AccountId, campaignId: CampaignId): Outcome<List<PlayerCharacterOverview>, CreatureError>

    suspend fun byId(requester: AccountId, playerCharacterId: PlayerCharacterId): Outcome<PlayerCharacter, CreatureError>
}
