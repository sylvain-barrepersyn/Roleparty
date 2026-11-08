package app.roleparty.media.domain.model

import app.roleparty.media.commons.ImageFormat

sealed interface MediaError {

    data class ImageTooLarge(val maxBytes: Int) : MediaError

    data class InvalidImage(val format: ImageFormat) : MediaError
}
