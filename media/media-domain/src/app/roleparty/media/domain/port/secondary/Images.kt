package app.roleparty.media.domain.port.secondary

import app.roleparty.media.commons.ImageId
import app.roleparty.media.domain.model.Image

interface Images {

    suspend fun save(image: Image)

    suspend fun findById(imageId: ImageId): Image?
}
