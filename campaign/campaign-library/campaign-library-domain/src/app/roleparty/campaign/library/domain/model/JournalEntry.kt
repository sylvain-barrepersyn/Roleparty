package app.roleparty.campaign.library.domain.model

import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.library.commons.JournalEntryId
import app.roleparty.media.commons.ImageId

data class JournalEntry(
    val id: JournalEntryId,
    val playerCharacterId: PlayerCharacterId,
    val content: Content,
) {

    data class Content(val title: String, val text: String, val images: List<ImageId>) {

        companion object {
            val EMPTY = Content("", "", emptyList())
        }
    }
}
