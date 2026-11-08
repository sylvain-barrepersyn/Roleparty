package app.roleparty.campaign.domain.model

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.commons.PlayerCharacterId
import app.roleparty.campaign.domain.model.playercharacter.classes.CharacterClass

sealed interface CampaignError {

    data class CampaignNotFound(val campaignId: CampaignId) : CampaignError

    data object GameMasterRequired : CampaignError

    data object ParticipantRequired : CampaignError

    data object BlankName : CampaignError

    data object InvalidInvitationCode : CampaignError

    data object InvitationCodeAlreadyUsed : CampaignError

    data object AlreadyParticipant : CampaignError

    data class PlayerCharacterNotFound(val playerCharacterId: PlayerCharacterId) : CampaignError

    data object PlayerCharacterOwnerRequired : CampaignError

    data class WrongSkillCount(val expected: Int) : CampaignError

    data class ClassNotHeld(val characterClass: CharacterClass) : CampaignError

    data class MaxLevelReached(val characterClass: CharacterClass) : CampaignError
}
