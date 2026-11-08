package app.roleparty.media.test

import app.roleparty.core.test.shouldBeSuccess
import app.roleparty.media.commons.ImageFormat
import app.roleparty.media.commons.ImageId
import app.roleparty.media.domain.MediaModule

class MediaFixture {

    private val images = InMemoryImages()

    val module = MediaModule(images)

    val uploadImage = module.uploadImage
    val findImage = module.findImage

    suspend fun `given an image`(content: ByteArray = JPEG_CONTENT): ImageId = uploadImage(content, ImageFormat.JPEG).shouldBeSuccess()

    fun storedImages() = images.count()

    companion object {
        val JPEG_CONTENT = byteArrayOf(0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xE0.toByte(), 0x00, 0x10, 0x4A, 0x46, 0x49, 0x46)
    }
}
