package app.roleparty.campaign.tabletop.infrastructure.adapter.primary

import app.roleparty.campaign.tabletop.contract.web.AttendeeBody
import app.roleparty.campaign.tabletop.contract.web.StartedGameSessionBody
import app.roleparty.campaign.tabletop.domain.model.Attendee
import app.roleparty.campaign.tabletop.domain.model.Occupant
import app.roleparty.campaign.tabletop.domain.model.StartedGameSession

internal fun StartedGameSession.toBody() = StartedGameSessionBody(gameSession.id, gameSession.date, gameSession.name)

internal fun Attendee.toBody() = when (val occupant = seat.occupant) {
    Occupant.GameMaster -> AttendeeBody(seat.accountId, gameMaster = true, camera = camera, microphone = microphone)
    is Occupant.Player -> AttendeeBody(seat.accountId, gameMaster = false, occupant.characterName, occupant.imageId, camera, microphone)
}
