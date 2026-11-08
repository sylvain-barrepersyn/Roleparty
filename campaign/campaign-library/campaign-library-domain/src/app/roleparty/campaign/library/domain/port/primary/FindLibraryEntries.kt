package app.roleparty.campaign.library.domain.port.primary

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.domain.model.LibraryEntry
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.core.commons.Outcome

interface FindLibraryEntries {

    suspend fun ofCampaign(requester: AccountId, campaignId: CampaignId): Outcome<List<LibraryEntry>, LibraryError>
}
