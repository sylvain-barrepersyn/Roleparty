package app.roleparty.campaign.tabletop.frontend.container

import app.roleparty.campaign.creature.commons.PlayerCharacterId

sealed interface TabletopParticipation {

    val playerCharacterId: PlayerCharacterId?

    data object GameMaster : TabletopParticipation {
        override val playerCharacterId: PlayerCharacterId? = null
    }

    data class Player(override val playerCharacterId: PlayerCharacterId) : TabletopParticipation
}
