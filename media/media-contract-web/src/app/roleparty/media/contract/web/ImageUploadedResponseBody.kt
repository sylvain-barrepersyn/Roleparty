package app.roleparty.media.contract.web

import app.roleparty.media.commons.ImageId
import kotlinx.serialization.Serializable

@Serializable
data class ImageUploadedResponseBody(val id: ImageId)
