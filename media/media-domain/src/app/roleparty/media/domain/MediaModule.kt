package app.roleparty.media.domain

import app.roleparty.media.domain.port.primary.FindImage
import app.roleparty.media.domain.port.primary.UploadImage
import app.roleparty.media.domain.port.secondary.Images
import app.roleparty.media.domain.service.ImageFindingService
import app.roleparty.media.domain.service.ImageUploadingService

class MediaModule(images: Images) {

    val uploadImage: UploadImage = ImageUploadingService(images)

    val findImage: FindImage = ImageFindingService(images)
}
