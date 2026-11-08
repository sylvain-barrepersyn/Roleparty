package app.roleparty.media.domain.port.primary

import app.roleparty.core.commons.Outcome
import app.roleparty.media.commons.ImageFormat
import app.roleparty.media.commons.ImageId
import app.roleparty.media.domain.model.MediaError

interface UploadImage {

    suspend operator fun invoke(content: ByteArray, format: ImageFormat): Outcome<ImageId, MediaError>
}
