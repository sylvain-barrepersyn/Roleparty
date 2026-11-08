package app.roleparty.media.domain.service

import app.roleparty.media.commons.ImageId
import app.roleparty.media.domain.port.primary.FindImage
import app.roleparty.media.domain.port.secondary.Images

internal class ImageFindingService(private val images: Images) : FindImage {

    override suspend fun byId(imageId: ImageId) = images.findById(imageId)
}
