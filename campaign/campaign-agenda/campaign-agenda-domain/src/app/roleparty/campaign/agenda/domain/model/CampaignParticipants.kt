package app.roleparty.campaign.agenda.domain.model

import app.roleparty.account.commons.AccountId

data class CampaignParticipants(val gameMaster: AccountId, val players: Set<AccountId>) {

    operator fun contains(accountId: AccountId) = accountId == gameMaster || accountId in players
}
