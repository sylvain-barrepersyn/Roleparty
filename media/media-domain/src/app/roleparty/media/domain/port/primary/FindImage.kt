package app.roleparty.media.domain.port.primary

import app.roleparty.media.commons.ImageId
import app.roleparty.media.domain.model.Image

interface FindImage {

    suspend fun byId(imageId: ImageId): Image?
}
