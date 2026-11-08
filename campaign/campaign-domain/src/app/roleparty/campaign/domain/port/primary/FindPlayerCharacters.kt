package app.roleparty.campaign.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.commons.PlayerCharacterId
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.domain.model.playercharacter.PlayerCharacter
import app.roleparty.campaign.domain.model.playercharacter.PlayerCharacterOverview
import app.roleparty.core.commons.Outcome

interface FindPlayerCharacters {

    suspend fun ofRequester(requester: AccountId, campaignId: CampaignId): Outcome<List<PlayerCharacterOverview>, CampaignError>

    suspend fun byId(requester: AccountId, playerCharacterId: PlayerCharacterId): Outcome<PlayerCharacter, CampaignError>
}
