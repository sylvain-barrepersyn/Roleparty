package app.roleparty.campaign.tabletop.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.tabletop.domain.model.Seat
import app.roleparty.campaign.tabletop.domain.model.TabletopError
import app.roleparty.core.commons.Outcome

interface TakeSeat {

    suspend operator fun invoke(requester: AccountId, campaignId: CampaignId, playerCharacterId: PlayerCharacterId?): Outcome<Seat, TabletopError>
}
