package app.roleparty.campaign.tabletop.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.tabletop.domain.model.Occupant
import app.roleparty.campaign.tabletop.domain.model.Seat
import app.roleparty.campaign.tabletop.domain.model.TabletopError
import app.roleparty.campaign.tabletop.domain.port.primary.TakeSeat
import app.roleparty.campaign.tabletop.domain.port.secondary.PlayerCharacters
import app.roleparty.campaign.tabletop.domain.port.secondary.Tabletops
import app.roleparty.core.commons.Outcome

internal class SeatTakingService(
    private val tabletops: Tabletops,
    private val playerCharacters: PlayerCharacters,
    private val access: CampaignAccessCheckingService,
) : TakeSeat {

    override suspend fun invoke(requester: AccountId, campaignId: CampaignId, playerCharacterId: PlayerCharacterId?): Outcome<Seat, TabletopError> {
        val participants = when (val outcome = access.asParticipant(requester, campaignId)) {
            is Outcome.Failure -> return outcome
            is Outcome.Success -> outcome.value
        }
        tabletops.ofCampaign(campaignId) ?: return Outcome.Failure(TabletopError.GameSessionNotStarted)
        if (requester == participants.gameMaster) return Outcome.Success(Seat(requester, Occupant.GameMaster))

        playerCharacterId ?: return Outcome.Failure(TabletopError.PlayerCharacterRequired)
        val character = playerCharacters.findById(playerCharacterId)?.takeIf { it.campaignId == campaignId && it.player == requester }
            ?: return Outcome.Failure(TabletopError.PlayerCharacterNotFound(playerCharacterId))
        return Outcome.Success(Seat(requester, Occupant.Player(character.id, character.name, character.imageId)))
    }
}
