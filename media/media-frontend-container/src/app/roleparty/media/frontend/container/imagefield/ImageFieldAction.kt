package app.roleparty.media.frontend.container.imagefield

import app.roleparty.core.frontend.container.ViewModelAction
import io.github.vinceglb.filekit.PlatformFile

sealed interface ImageFieldAction : ViewModelAction {

    data class Upload(val file: PlatformFile) : ImageFieldAction
}
