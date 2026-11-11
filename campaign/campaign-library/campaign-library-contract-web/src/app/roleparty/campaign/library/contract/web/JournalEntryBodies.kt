package app.roleparty.campaign.library.contract.web

import app.roleparty.campaign.library.commons.JournalEntryId
import app.roleparty.media.commons.ImageId
import kotlinx.serialization.Serializable

@Serializable
data class JournalEntryCreatedResponseBody(val id: JournalEntryId)

@Serializable
data class JournalEntryRequestBody(val title: String, val text: String, val images: List<ImageId>)

@Serializable
data class JournalEntryResponseBody(val id: JournalEntryId, val title: String, val text: String, val images: List<ImageId>)
