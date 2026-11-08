package app.roleparty.media.domain.service

import app.roleparty.core.commons.Outcome
import app.roleparty.media.commons.ImageFormat
import app.roleparty.media.commons.ImageId
import app.roleparty.media.domain.model.Image
import app.roleparty.media.domain.model.MediaError
import app.roleparty.media.domain.port.primary.UploadImage
import app.roleparty.media.domain.port.secondary.Images

internal class ImageUploadingService(private val images: Images) : UploadImage {

    override suspend fun invoke(content: ByteArray, format: ImageFormat): Outcome<ImageId, MediaError> {
        if (content.size > Image.MAX_BYTES) return Outcome.Failure(MediaError.ImageTooLarge(Image.MAX_BYTES))
        if (!content.startsWith(format.signature)) return Outcome.Failure(MediaError.InvalidImage(format))

        val image = Image(ImageId.generate(), content, format)
        images.save(image)
        return Outcome.Success(image.id)
    }

    private val ImageFormat.signature: ByteArray
        get() = when (this) {
            ImageFormat.JPEG -> JPEG_SIGNATURE
        }

    private fun ByteArray.startsWith(prefix: ByteArray) = size >= prefix.size && prefix.indices.all { this[it] == prefix[it] }

    private companion object {
        val JPEG_SIGNATURE = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte())
    }
}
