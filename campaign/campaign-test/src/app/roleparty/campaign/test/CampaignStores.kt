package app.roleparty.campaign.test

import app.roleparty.campaign.domain.port.secondary.Campaigns
import app.roleparty.campaign.domain.port.secondary.Invitations
import app.roleparty.campaign.domain.port.secondary.PlayerCharacters

class CampaignStores(
    val campaigns: Campaigns,
    val invitations: Invitations,
    val playerCharacters: PlayerCharacters,
) {
    companion object {
        internal fun inMemory(): CampaignStores {
            val invitations = InMemoryInvitations()
            return CampaignStores(InMemoryCampaigns(invitations), invitations, InMemoryPlayerCharacters())
        }
    }
}
