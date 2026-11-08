package app.roleparty.campaign.domain

import app.roleparty.campaign.domain.port.primary.CreateCampaign
import app.roleparty.campaign.domain.port.primary.CreatePlayerCharacter
import app.roleparty.campaign.domain.port.primary.FindCampaigns
import app.roleparty.campaign.domain.port.primary.FindPlayerCharacters
import app.roleparty.campaign.domain.port.primary.InviteToCampaign
import app.roleparty.campaign.domain.port.primary.JoinCampaign
import app.roleparty.campaign.domain.port.primary.LevelUpPlayerCharacter
import app.roleparty.campaign.domain.port.secondary.Campaigns
import app.roleparty.campaign.domain.port.secondary.Invitations
import app.roleparty.campaign.domain.port.secondary.PlayerCharacters
import app.roleparty.campaign.domain.service.CampaignAccessCheckingService
import app.roleparty.campaign.domain.service.CampaignCreatingService
import app.roleparty.campaign.domain.service.CampaignFindingService
import app.roleparty.campaign.domain.service.CampaignInvitingService
import app.roleparty.campaign.domain.service.CampaignJoiningService
import app.roleparty.campaign.domain.service.PlayerCharacterCreatingService
import app.roleparty.campaign.domain.service.PlayerCharacterFindingService
import app.roleparty.campaign.domain.service.PlayerCharacterLevelingUpService

class CampaignModule(
    campaigns: Campaigns,
    invitations: Invitations,
    playerCharacters: PlayerCharacters,
) {

    private val access = CampaignAccessCheckingService(campaigns)

    val createCampaign: CreateCampaign = CampaignCreatingService(campaigns)

    val findCampaigns: FindCampaigns = CampaignFindingService(campaigns, access)

    val inviteToCampaign: InviteToCampaign = CampaignInvitingService(invitations, access)

    val joinCampaign: JoinCampaign = CampaignJoiningService(campaigns, invitations)

    val createPlayerCharacter: CreatePlayerCharacter = PlayerCharacterCreatingService(playerCharacters, access)

    val findPlayerCharacters: FindPlayerCharacters = PlayerCharacterFindingService(playerCharacters, campaigns, access)

    val levelUpPlayerCharacter: LevelUpPlayerCharacter = PlayerCharacterLevelingUpService(playerCharacters)
}
