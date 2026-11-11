package app.roleparty.campaign.story.domain.model

import app.roleparty.core.document.Document
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.story.commons.SceneId

data class Scene(
    val id: SceneId,
    val campaignId: CampaignId,
    val name: String,
    val description: Document,
    val libraryEntries: List<LibraryEntryId> = emptyList(),
)
