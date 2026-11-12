package app.roleparty.campaign.frontend.component.played

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.frontend.component.resources.*
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.navigation.SidenavItem
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.media.frontend.component.ImageThumbnail
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

enum class PlayedCampaignSection(internal val icon: IconVector, internal val label: StringResource, internal val top: Boolean = true) {
    AGENDA(IconVector.CALENDAR, Res.string.section_agenda),
    SHEET(IconVector.CHARACTER_SHEET, Res.string.section_sheet),
    JOURNAL(IconVector.JOURNAL, Res.string.section_journal),
    SETTINGS(IconVector.SETTINGS, Res.string.section_settings, false),
}

@Composable
fun PlayedCampaignLayout(
    characterName: String,
    characterLevel: Int,
    avatarUrl: String,
    section: PlayedCampaignSection,
    onSectionSelect: (PlayedCampaignSection) -> Unit,
    onExit: () -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    Row(modifier = modifier.fillMaxSize().padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(
            modifier = Modifier.width(88.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ImageThumbnail(
                url = avatarUrl,
                contentDescription = characterName,
                modifier = Modifier.size(width = 88.dp, height = 110.dp),
                placeholder = IconVector.CHARACTER_SHEET,
            )
            PlayedCampaignSection.entries.filter { it.top }.forEach {
                SidenavItem(icon = it.icon, label = stringResource(it.label), selected = it == section, onClick = { onSectionSelect(it) })
            }
            Spacer(Modifier.weight(1f))
            PlayedCampaignSection.entries.filter { !it.top }.forEach {
                SidenavItem(icon = it.icon, label = stringResource(it.label), selected = it == section, onClick = { onSectionSelect(it) })
            }
            SidenavItem(icon = IconVector.PREVIOUS, label = stringResource(Res.string.exit), selected = false, onClick = onExit)
        }
        Column(modifier = Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.height(64.dp), verticalArrangement = Arrangement.Center) {
                    Text(text = characterName, style = Theme.typography.headline, maxLines = 1)
                    Text(
                        text = stringResource(Res.string.character_level, characterLevel),
                        style = Theme.typography.subtitle
                    )
                }
                Spacer(Modifier.weight(1f))
                actions()
            }
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), content = content)
        }
    }
}
