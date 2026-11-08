package app.roleparty.media.frontend.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.DpSize
import app.roleparty.core.designsystem.button.Button
import app.roleparty.core.designsystem.field.Field
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.media.commons.ImageFormat
import app.roleparty.media.frontend.component.resources.Res
import app.roleparty.media.frontend.component.resources.choose_image
import app.roleparty.media.frontend.component.resources.unexpected
import app.roleparty.media.frontend.component.resources.unreachable
import app.roleparty.media.frontend.component.resources.uploading_image
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

sealed interface ImageFieldFailure {
    data class Rejected(val message: String) : ImageFieldFailure
    data object Unreachable : ImageFieldFailure
    data object Unexpected : ImageFieldFailure
}

@Composable
fun ImageField(
    label: String,
    imageUrl: String?,
    uploading: Boolean,
    failure: ImageFieldFailure?,
    onFilePicked: (PlatformFile) -> Unit,
    modifier: Modifier = Modifier,
    imageSize: DpSize = DpSize.Unspecified,
    required: Boolean = false,
) {
    val scope = rememberCoroutineScope()
    val extensions = ImageFormat.entries.flatMap { it.extensions }.toSet()

    Field(label = label, modifier = modifier, enabled = !uploading, required = required, error = failure?.message()) {
        Column {
            if (imageUrl != null) {
                RemoteImage(url = imageUrl, contentDescription = label, modifier = Modifier.size(imageSize), contentScale = ContentScale.Crop)
            }
            Button(
                leadingIcon = IconVector.UPLOAD,
                text = stringResource(if (uploading) Res.string.uploading_image else Res.string.choose_image),
                onClick = { scope.launch { FileKit.openFilePicker(FileKitType.File(extensions))?.let(onFilePicked) } },
                enabled = !uploading,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun ImageFieldFailure.message() = when (this) {
    is ImageFieldFailure.Rejected -> message
    ImageFieldFailure.Unreachable -> stringResource(Res.string.unreachable)
    ImageFieldFailure.Unexpected -> stringResource(Res.string.unexpected)
}
