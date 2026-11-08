package app.roleparty.campaign.agenda.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.core.commons.Outcome
import kotlinx.datetime.LocalDate

interface ScheduleGameSession {

    suspend operator fun invoke(requester: AccountId, campaignId: CampaignId, date: LocalDate): Outcome<GameSessionId, AgendaError>
}
