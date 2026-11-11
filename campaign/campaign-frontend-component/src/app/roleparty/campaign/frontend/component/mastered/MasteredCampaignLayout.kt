package app.roleparty.campaign.frontend.component.mastered

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.frontend.component.CampaignImage
import app.roleparty.campaign.frontend.component.resources.*
import app.roleparty.core.designsystem.button.IconButton
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.navigation.SidenavItem
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

enum class MasteredCampaignSection(internal val icon: IconVector, internal val label: StringResource, internal val top: Boolean = true) {
    AGENDA(IconVector.CALENDAR, Res.string.section_agenda),
    SCENES(IconVector.INK_PEN, Res.string.section_scenes),
    LIBRARY(IconVector.PRINT_STACK, Res.string.section_library),
    CREATURES(IconVector.PUPPET, Res.string.section_creatures),
    SETTINGS(IconVector.SETTINGS, Res.string.section_settings, false),
}

@Composable
fun MasteredCampaignLayout(
    name: String,
    players: Int,
    imageUrl: String,
    section: MasteredCampaignSection,
    onSectionSelect: (MasteredCampaignSection) -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Row(modifier = modifier.fillMaxSize().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(
            modifier = Modifier.width(88.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CampaignImage(url = imageUrl, contentDescription = name, size = 88.dp)
            MasteredCampaignSection.entries.filter { it.top }.forEach {
                SidenavItem(
                    icon = it.icon,
                    label = stringResource(it.label),
                    selected = it == section,
                    onClick = { onSectionSelect(it) })
            }
            Spacer(Modifier.weight(1f))

            MasteredCampaignSection.entries.filter { !it.top }.forEach {
                SidenavItem(
                    icon = it.icon,
                    label = stringResource(it.label),
                    selected = it == section,
                    onClick = { onSectionSelect(it) })
            }
            SidenavItem(
                icon = IconVector.PREVIOUS,
                label = stringResource(Res.string.exit),
                selected = false,
                onClick = onExit
            )
        }
        Column(modifier = Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.height(64.dp), verticalArrangement = Arrangement.Center) {
                    Text(text = name, style = Theme.typography.headline, maxLines = 1)
                    Text(
                        text = pluralStringResource(Res.plurals.players, players, players),
                        style = Theme.typography.subtitle
                    )
                }
                Spacer(Modifier.weight(1f))
            }
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), content = content)
        }
    }
}
