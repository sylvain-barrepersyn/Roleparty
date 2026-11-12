package app.roleparty.campaign.tabletop.frontend.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.tabletop.contract.web.AttendeeBody
import app.roleparty.campaign.tabletop.frontend.component.resources.Res
import app.roleparty.campaign.tabletop.frontend.component.resources.camera_unavailable
import app.roleparty.campaign.tabletop.frontend.component.resources.game_master
import app.roleparty.campaign.tabletop.frontend.component.resources.joining
import app.roleparty.campaign.tabletop.frontend.component.resources.microphone_unavailable
import app.roleparty.campaign.tabletop.frontend.component.resources.turn_camera_off
import app.roleparty.campaign.tabletop.frontend.component.resources.turn_camera_on
import app.roleparty.campaign.tabletop.frontend.component.resources.turn_microphone_off
import app.roleparty.campaign.tabletop.frontend.component.resources.turn_microphone_on
import app.roleparty.campaign.tabletop.frontend.component.resources.waiting_for_game_master
import app.roleparty.core.designsystem.button.Button
import app.roleparty.core.designsystem.feedback.Callout
import app.roleparty.core.designsystem.feedback.CalloutKind
import app.roleparty.core.designsystem.feedback.Loader
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.layout.CenteredBox
import app.roleparty.core.designsystem.surface.Surface
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.media.commons.ImageId
import app.roleparty.media.frontend.component.ImageThumbnail
import org.jetbrains.compose.resources.stringResource

private val PEOPLE_WIDTH = 240.dp
private const val VIDEO_RATIO = 16f / 9f
private const val AVATAR_RATIO = 4f / 5f

@Composable
fun Tabletop(
    attendees: List<AttendeeBody>,
    camera: Boolean,
    microphone: Boolean,
    cameraUnavailable: Boolean,
    microphoneUnavailable: Boolean,
    imageUrl: (ImageId) -> String,
    video: @Composable (AccountId) -> ImageBitmap?,
    onCameraChange: (Boolean) -> Unit,
    onMicrophoneChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(modifier = Modifier.weight(1f).fillMaxHeight()) {
                if (attendees.isEmpty()) CenteredBox { Text(stringResource(Res.string.waiting_for_game_master)) }
            }
            Column(
                modifier = Modifier.width(PEOPLE_WIDTH).fillMaxHeight().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                attendees.forEach { attendee ->
                    key(attendee.accountId) {
                        AttendeeTile(attendee = attendee, frame = video(attendee.accountId), imageUrl = imageUrl)
                    }
                }
            }
        }
        if (cameraUnavailable) Callout(text = stringResource(Res.string.camera_unavailable), kind = CalloutKind.ERROR, modifier = Modifier.fillMaxWidth())
        if (microphoneUnavailable) Callout(text = stringResource(Res.string.microphone_unavailable), kind = CalloutKind.ERROR, modifier = Modifier.fillMaxWidth())
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Button(
                leadingIcon = IconVector.CAMERA,
                onClick = { onCameraChange(!camera) },
                text = stringResource(if (camera) Res.string.turn_camera_off else Res.string.turn_camera_on),
            )
            Button(
                leadingIcon = IconVector.SPEAK,
                onClick = { onMicrophoneChange(!microphone) },
                text = stringResource(if (microphone) Res.string.turn_microphone_off else Res.string.turn_microphone_on),
            )
        }
    }
}

@Composable
fun TabletopJoining(modifier: Modifier = Modifier) {
    CenteredBox(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Loader()
            Text(stringResource(Res.string.joining))
        }
    }
}

@Composable
private fun AttendeeTile(attendee: AttendeeBody, frame: ImageBitmap?, imageUrl: (ImageId) -> String) {
    val name = if (attendee.gameMaster) stringResource(Res.string.game_master) else attendee.characterName
    val avatarUrl = attendee.imageId?.let(imageUrl)

    Surface(
        modifier = Modifier.fillMaxWidth().aspectRatio(VIDEO_RATIO),
        shape = Theme.shapes.roundedSmall,
        contentAlignment = Alignment.Center,
    ) {
        when {
            attendee.camera && frame != null -> {
                Image(bitmap = frame, contentDescription = name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                if (!attendee.gameMaster) {
                    ImageThumbnail(
                        url = avatarUrl,
                        contentDescription = name,
                        modifier = Modifier.align(Alignment.BottomStart).padding(8.dp).size(width = 32.dp, height = 40.dp),
                        placeholder = IconVector.CHARACTER_SHEET,
                    )
                }
            }

            attendee.gameMaster -> Text(text = stringResource(Res.string.game_master), style = Theme.typography.title, maxLines = 1)

            else -> ImageThumbnail(
                url = avatarUrl,
                contentDescription = name,
                modifier = Modifier.fillMaxHeight().aspectRatio(AVATAR_RATIO),
                shape = Theme.shapes.rectangle,
                placeholder = IconVector.CHARACTER_SHEET,
            )
        }
    }
}
