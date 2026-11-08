package app.roleparty.campaign.library.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.domain.model.LibraryEntry
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.campaign.library.domain.port.primary.FindLibraryEntries
import app.roleparty.campaign.library.domain.port.secondary.LibraryEntries
import app.roleparty.core.commons.Outcome

internal class LibraryEntryFindingService(
    private val libraryEntries: LibraryEntries,
    private val gameMaster: GameMasterCheckingService,
) : FindLibraryEntries {

    override suspend fun ofCampaign(requester: AccountId, campaignId: CampaignId): Outcome<List<LibraryEntry>, LibraryError> {
        gameMaster.check(requester, campaignId)?.let { return Outcome.Failure(it) }

        return Outcome.Success(libraryEntries.ofCampaign(campaignId))
    }
}
