package app.roleparty.media.domain.model

import app.roleparty.media.commons.ImageFormat
import app.roleparty.media.commons.ImageId

class Image(
    val id: ImageId,
    val content: ByteArray,
    val format: ImageFormat,
) {

    override fun equals(other: Any?) =
        other is Image && id == other.id && format == other.format && content.contentEquals(other.content)

    override fun hashCode() = 31 * (31 * id.hashCode() + format.hashCode()) + content.contentHashCode()

    override fun toString() = "Image(id=$id, format=$format, bytes=${content.size})"

    companion object {
        const val MAX_BYTES = 10 * 1024 * 1024
    }
}
