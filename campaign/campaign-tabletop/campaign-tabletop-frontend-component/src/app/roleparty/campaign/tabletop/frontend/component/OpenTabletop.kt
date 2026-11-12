package app.roleparty.campaign.tabletop.frontend.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.tabletop.contract.web.AttendeeBody
import app.roleparty.campaign.tabletop.frontend.component.resources.Res
import app.roleparty.campaign.tabletop.frontend.component.resources.open_tabletop
import app.roleparty.core.designsystem.icon.Icon
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.surface.Surface
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.media.commons.ImageId
import app.roleparty.media.frontend.component.ImageThumbnail
import org.jetbrains.compose.resources.stringResource

@Composable
fun OpenTabletop(
    attendees: List<AttendeeBody>,
    imageUrl: (ImageId) -> String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        role = Role.Button,
        shape = Theme.shapes.roundedSmall,
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(vector = IconVector.TABLETOP, tint = Theme.colors.primary)
            Text(text = stringResource(Res.string.open_tabletop), color = Theme.colors.textPrimary, maxLines = 1)
            attendees.filter { !it.gameMaster }.forEach { attendee ->
                key(attendee.accountId) {
                    ImageThumbnail(
                        url = attendee.imageId?.let(imageUrl),
                        contentDescription = attendee.characterName,
                        modifier = Modifier.size(width = 32.dp, height = 40.dp),
                        placeholder = IconVector.CHARACTER_SHEET,
                    )
                }
            }
        }
    }
}
