package app.roleparty.campaign.agenda.domain.model

data class AgendaPreference(val presence: Presence, val digitalGamingTable: Boolean) {

    companion object {
        fun defaultFor(gameMaster: Boolean) =
            AgendaPreference(if (gameMaster) Presence.IN_PERSON_WITH_COMPUTER else Presence.ABSENT, digitalGamingTable = false)
    }
}
