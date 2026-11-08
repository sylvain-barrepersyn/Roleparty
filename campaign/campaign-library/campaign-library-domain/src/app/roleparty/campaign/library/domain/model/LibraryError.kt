package app.roleparty.campaign.library.domain.model

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId

sealed interface LibraryError {

    data class CampaignNotFound(val campaignId: CampaignId) : LibraryError

    data object GameMasterRequired : LibraryError

    data object BlankTitle : LibraryError

    data class LibraryEntryNotFound(val libraryEntryId: LibraryEntryId) : LibraryError
}
