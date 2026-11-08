package app.roleparty.media.frontend.container.imagefield

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import app.roleparty.core.frontend.container.Container
import app.roleparty.media.commons.ImageId
import app.roleparty.media.frontend.component.ImageField
import app.roleparty.media.frontend.container.data.MediaApi
import org.koin.compose.koinInject

class ImageFieldContainer(
    fieldId: String,
    private val label: String,
    private val value: ImageId?,
    private val onValueChange: (ImageId) -> Unit,
    private val modifier: Modifier = Modifier,
    private val imageSize: DpSize = DpSize.Unspecified,
    private val required: Boolean = false,
) : Container<ImageFieldState, ImageFieldAction, ImageFieldEvent, ImageFieldViewModel>(ImageFieldViewModel::class, fieldId) {

    @Composable
    override fun Content(state: ImageFieldState, handle: (ImageFieldAction) -> Unit) {
        val api = koinInject<MediaApi>()
        ImageField(
            label = label,
            imageUrl = value?.let(api::imageUrl),
            uploading = state == ImageFieldState.Uploading,
            failure = (state as? ImageFieldState.Failed)?.failure,
            onFilePicked = { handle(ImageFieldAction.Upload(it)) },
            modifier = modifier,
            imageSize = imageSize,
            required = required,
        )
    }

    override fun onEvent(event: ImageFieldEvent) = when (event) {
        is ImageFieldEvent.ImageUploaded -> onValueChange(event.imageId)
    }
}
