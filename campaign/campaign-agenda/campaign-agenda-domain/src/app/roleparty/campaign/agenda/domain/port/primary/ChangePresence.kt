package app.roleparty.campaign.agenda.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.domain.model.Presence
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.commons.Outcome

interface ChangePresence {

    suspend operator fun invoke(
        requester: AccountId,
        campaignId: CampaignId,
        gameSessionId: GameSessionId,
        participant: AccountId,
        presence: Presence,
    ): Outcome<Unit, AgendaError>
}
