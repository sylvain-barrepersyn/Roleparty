package app.roleparty.campaign.library.contract.web

import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.media.commons.ImageId
import kotlinx.serialization.Serializable

@Serializable
data class LibraryEntryCreationRequestBody(val title: String, val description: String, val imageId: ImageId)

@Serializable
data class LibraryEntryMetadataRequestBody(val title: String, val description: String)

@Serializable
data class LibraryEntryCreatedResponseBody(val id: LibraryEntryId)

@Serializable
data class LibraryEntryResponseBody(val id: LibraryEntryId, val title: String, val description: String, val imageId: ImageId)
