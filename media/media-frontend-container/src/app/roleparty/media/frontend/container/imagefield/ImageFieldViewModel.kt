package app.roleparty.media.frontend.container.imagefield

import androidx.lifecycle.viewModelScope
import app.roleparty.core.commons.Outcome
import app.roleparty.core.frontend.container.ViewModel
import app.roleparty.media.commons.ImageFormat
import app.roleparty.media.frontend.component.ImageFieldFailure
import app.roleparty.media.frontend.container.data.MediaApi
import app.roleparty.media.frontend.container.data.MediaFailure
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.readBytes
import java.io.IOException
import kotlinx.coroutines.launch

class ImageFieldViewModel(private val api: MediaApi) :
    ViewModel<ImageFieldState, ImageFieldAction, ImageFieldEvent>(ImageFieldState.Idle) {

    override fun handle(action: ImageFieldAction) {
        when (action) {
            is ImageFieldAction.Upload -> upload(action.file)
        }
    }

    private fun upload(file: PlatformFile) {
        if (state.value == ImageFieldState.Uploading) return
        val format = ImageFormat.entries.firstOrNull { file.extension.lowercase() in it.extensions }
            ?: return update { ImageFieldState.Failed(ImageFieldFailure.Unexpected) }

        update { ImageFieldState.Uploading }
        viewModelScope.launch {
            val content = try {
                file.readBytes()
            } catch (_: IOException) {
                return@launch update { ImageFieldState.Failed(ImageFieldFailure.Unexpected) }
            }

            when (val outcome = api.upload(content, format)) {
                is Outcome.Success -> {
                    update { ImageFieldState.Idle }
                    emit(ImageFieldEvent.ImageUploaded(outcome.value))
                }
                is Outcome.Failure -> update { ImageFieldState.Failed(outcome.error.toImageFieldFailure()) }
            }
        }
    }

    private fun MediaFailure.toImageFieldFailure(): ImageFieldFailure = when (this) {
        is MediaFailure.Rejected -> ImageFieldFailure.Rejected(message)
        MediaFailure.Unreachable -> ImageFieldFailure.Unreachable
        MediaFailure.Unexpected -> ImageFieldFailure.Unexpected
    }
}
