package app.roleparty.media.commons

import kotlinx.serialization.Serializable

@Serializable
enum class ImageFormat(val mediaType: String, val extensions: Set<String>) {
    JPEG("image/jpeg", setOf("jpg", "jpeg")),
    ;

    companion object {
        fun ofMediaType(mediaType: String) = entries.firstOrNull { it.mediaType.equals(mediaType, ignoreCase = true) }
    }
}
