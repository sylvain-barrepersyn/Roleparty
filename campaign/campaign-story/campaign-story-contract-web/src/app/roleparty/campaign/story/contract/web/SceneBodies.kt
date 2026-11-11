package app.roleparty.campaign.story.contract.web

import app.roleparty.core.document.Document
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.story.commons.SceneId
import kotlinx.serialization.Serializable

@Serializable
data class SceneRequestBody(val name: String, val description: Document)

@Serializable
data class SceneCreatedResponseBody(val id: SceneId)

@Serializable
data class SceneResponseBody(val id: SceneId, val name: String, val description: Document, val libraryEntryIds: List<LibraryEntryId>)
