package app.roleparty.campaign.tabletop.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.tabletop.domain.model.StartedGameSession
import app.roleparty.campaign.tabletop.domain.model.TabletopError
import app.roleparty.core.commons.Outcome

interface StartGameSession {

    suspend operator fun invoke(requester: AccountId, campaignId: CampaignId, gameSessionId: GameSessionId): Outcome<StartedGameSession, TabletopError>
}
