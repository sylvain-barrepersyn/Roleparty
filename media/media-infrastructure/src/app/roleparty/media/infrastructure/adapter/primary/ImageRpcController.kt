package app.roleparty.media.infrastructure.adapter.primary

import app.roleparty.core.commons.Outcome
import app.roleparty.media.commons.ImageFormat
import app.roleparty.media.commons.ImageId
import app.roleparty.media.contract.internal.ImageUploadingRpcService
import app.roleparty.media.domain.port.primary.UploadImage

internal class ImageRpcController(private val uploadImage: UploadImage) : ImageUploadingRpcService {

    override suspend fun upload(content: ByteArray, format: ImageFormat): ImageId? =
        when (val outcome = uploadImage(content, format)) {
            is Outcome.Success -> outcome.value
            is Outcome.Failure -> null
        }

    override suspend fun ping() = Unit
}
