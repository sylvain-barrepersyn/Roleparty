package app.roleparty.campaign.tabletop.domain.model

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.media.commons.ImageId

data class Seat(val accountId: AccountId, val occupant: Occupant) {

    val gameMaster get() = occupant is Occupant.GameMaster
}

sealed interface Occupant {

    data object GameMaster : Occupant

    data class Player(val playerCharacterId: PlayerCharacterId, val characterName: String, val imageId: ImageId) : Occupant
}
