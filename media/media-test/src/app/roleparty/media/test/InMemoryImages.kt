package app.roleparty.media.test

import app.roleparty.media.commons.ImageId
import app.roleparty.media.domain.model.Image
import app.roleparty.media.domain.port.secondary.Images

internal class InMemoryImages : Images {

    private val images = mutableMapOf<ImageId, Image>()

    override suspend fun save(image: Image) {
        images[image.id] = image
    }

    override suspend fun findById(imageId: ImageId) = images[imageId]

    fun count() = images.size
}
