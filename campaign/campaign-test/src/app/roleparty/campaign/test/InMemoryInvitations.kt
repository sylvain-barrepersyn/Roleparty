package app.roleparty.campaign.test

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.domain.model.Invitation
import app.roleparty.campaign.domain.port.secondary.Invitations

internal class InMemoryInvitations : Invitations {

    private val invitations = linkedMapOf<Invitation.Code, Invitation>()

    override suspend fun create(invitation: Invitation) {
        invitations[invitation.code] = invitation
    }

    override suspend fun findByCode(code: Invitation.Code) = invitations[code]

    override suspend fun ofCampaign(campaignId: CampaignId) = invitations.values.filter { it.campaignId == campaignId }

    fun markUsed(code: Invitation.Code): Boolean {
        val invitation = invitations[code]?.takeUnless { it.used } ?: return false
        invitations[code] = invitation.copy(used = true)
        return true
    }
}
