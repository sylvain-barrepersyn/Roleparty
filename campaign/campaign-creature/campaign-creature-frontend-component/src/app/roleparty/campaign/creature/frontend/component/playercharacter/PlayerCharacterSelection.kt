package app.roleparty.campaign.creature.frontend.component.playercharacter

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.creature.contract.web.PlayerCharacterOverviewResponseBody
import app.roleparty.campaign.creature.frontend.component.resources.*
import app.roleparty.core.designsystem.button.Button
import app.roleparty.core.designsystem.feedback.EmptyState
import app.roleparty.core.designsystem.feedback.Loader
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.surface.Surface
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.media.commons.ImageId
import app.roleparty.media.frontend.component.ImageThumbnail
import org.jetbrains.compose.resources.stringResource

@Composable
fun PlayerCharacterSelection(
    characters: List<PlayerCharacterOverviewResponseBody>?,
    error: String?,
    imageUrl: (ImageId) -> String,
    onSelect: (PlayerCharacterId) -> Unit,
    onCreate: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.widthIn(max = 700.dp).padding(64.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = stringResource(Res.string.choose_character), style = Theme.typography.headline, modifier = Modifier.weight(1f))
            if (characters == null && error == null) Loader()
        }

        ErrorCallout(error, Modifier.fillMaxWidth())

        when {
            characters == null -> Unit
            characters.isEmpty() -> EmptyState(text = stringResource(Res.string.no_characters), modifier = Modifier.fillMaxWidth())
            else -> Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                characters.forEach { character ->
                    PlayerCharacterCard(character, imageUrl(character.imageId), onClick = { onSelect(character.id) })
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(text = stringResource(Res.string.exit), onClick = onBack, leadingIcon = IconVector.PREVIOUS)
            Button(text = stringResource(Res.string.create_character), onClick = onCreate, leadingIcon = IconVector.ADD)
        }
    }
}

@Composable
private fun PlayerCharacterCard(character: PlayerCharacterOverviewResponseBody, imageUrl: String, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = Theme.shapes.roundedSmall) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ImageThumbnail(url = imageUrl, contentDescription = character.name, modifier = Modifier.size(width = 80.dp, height = 64.dp), placeholder = IconVector.CHARACTER_SHEET)
            Column(Modifier.weight(1f)) {
                Text(text = character.name, style = Theme.typography.title)
                Text(
                    text = listOf(stringResource(Res.string.character_level, character.level), character.classes.joinToString(" / ") { it.name }, character.species)
                        .filter { it.isNotBlank() }
                        .joinToString(" · "),
                    style = Theme.typography.bodySmall,
                )
            }
        }
    }
}
