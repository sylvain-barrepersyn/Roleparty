package app.roleparty.media.contract.web

import app.roleparty.media.commons.ImageId
import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/v1/images")
class ImagesResource {

    @Serializable
    @Resource("{imageId}")
    class Image(val imageId: ImageId, val parent: ImagesResource = ImagesResource())
}
