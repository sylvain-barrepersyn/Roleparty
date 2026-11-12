package app.roleparty.campaign.tabletop.domain.model

import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId

sealed interface TabletopError {

    data class CampaignNotFound(val campaignId: CampaignId) : TabletopError

    data object GameMasterRequired : TabletopError

    data object ParticipantRequired : TabletopError

    data class GameSessionNotFound(val gameSessionId: GameSessionId) : TabletopError

    data object GameSessionAlreadyStarted : TabletopError

    data object GameSessionNotStarted : TabletopError

    data object PlayerCharacterRequired : TabletopError

    data class PlayerCharacterNotFound(val playerCharacterId: PlayerCharacterId) : TabletopError
}
