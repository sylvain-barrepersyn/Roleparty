package app.roleparty.campaign.library.contract.web

import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/v1/campaigns/{campaignId}/library")
class LibraryResource(val campaignId: CampaignId) {

    @Serializable
    @Resource("{entryId}")
    class Entry(val parent: LibraryResource, val entryId: LibraryEntryId) {

        @Serializable
        @Resource("metadata")
        class Metadata(val parent: Entry)
    }
}
