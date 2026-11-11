package app.roleparty.campaign.agenda.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.agenda.domain.model.AgendaPreference
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.commons.Outcome

interface ChangeAgendaPreference {

    suspend operator fun invoke(requester: AccountId, campaignId: CampaignId, preference: AgendaPreference): Outcome<Unit, AgendaError>
}
