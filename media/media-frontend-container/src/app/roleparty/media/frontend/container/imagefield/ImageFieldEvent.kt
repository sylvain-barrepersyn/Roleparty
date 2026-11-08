package app.roleparty.media.frontend.container.imagefield

import app.roleparty.core.frontend.container.ViewModelEvent
import app.roleparty.media.commons.ImageId

sealed interface ImageFieldEvent : ViewModelEvent {

    data class ImageUploaded(val imageId: ImageId) : ImageFieldEvent
}
