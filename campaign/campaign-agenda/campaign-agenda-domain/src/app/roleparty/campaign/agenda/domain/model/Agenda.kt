package app.roleparty.campaign.agenda.domain.model

import app.roleparty.account.commons.AccountId

data class Agenda(val participants: List<Participant>, val gameSessions: List<GameSession>) {

    data class Participant(val accountId: AccountId, val gameMaster: Boolean, val characterNames: List<String>)
}
