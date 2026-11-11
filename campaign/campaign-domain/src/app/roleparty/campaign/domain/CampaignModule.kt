package app.roleparty.campaign.domain

import app.roleparty.campaign.domain.port.primary.CreateCampaign
import app.roleparty.campaign.domain.port.primary.FindCampaigns
import app.roleparty.campaign.domain.port.primary.InviteToCampaign
import app.roleparty.campaign.domain.port.primary.JoinCampaign
import app.roleparty.campaign.domain.port.secondary.Campaigns
import app.roleparty.campaign.domain.port.secondary.GameSessions
import app.roleparty.campaign.domain.port.secondary.Invitations
import app.roleparty.campaign.domain.service.CampaignAccessCheckingService
import app.roleparty.campaign.domain.service.CampaignCreatingService
import app.roleparty.campaign.domain.service.CampaignFindingService
import app.roleparty.campaign.domain.service.CampaignInvitingService
import app.roleparty.campaign.domain.service.CampaignJoiningService

class CampaignModule(
    campaigns: Campaigns,
    invitations: Invitations,
    gameSessions: GameSessions,
) {

    private val access = CampaignAccessCheckingService(campaigns)

    val createCampaign: CreateCampaign = CampaignCreatingService(campaigns)

    val findCampaigns: FindCampaigns = CampaignFindingService(campaigns, gameSessions, access)

    val inviteToCampaign: InviteToCampaign = CampaignInvitingService(invitations, access)

    val joinCampaign: JoinCampaign = CampaignJoiningService(campaigns, invitations)
}
