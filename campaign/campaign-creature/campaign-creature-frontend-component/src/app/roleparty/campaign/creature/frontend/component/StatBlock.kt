package app.roleparty.campaign.creature.frontend.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.creature.frontend.component.resources.*
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import org.jetbrains.compose.resources.stringResource

internal class AbilityStats(
    val label: String,
    val score: Int,
    val savingThrow: Int,
    val savingThrowProficient: Boolean,
    val skills: List<Pair<String, Int>>,
)

@Composable
internal fun RowScope.StatBox(label: String, value: String, detail: String?) {
    Column(
        modifier = Modifier.weight(1f).fillMaxHeight().subtleBorder().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = label, style = Theme.typography.bodySmall)
        Text(text = value, style = Theme.typography.bodyLarge, color = Theme.colors.textPrimary)
        Text(
            text = detail ?: stringResource(Res.string.none),
            style = Theme.typography.bodySmall,
            color = if (detail == null) Theme.colors.textSecondary else Theme.colors.textPrimary
        )
    }
}

@Composable
internal fun StatLine(label: String, value: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Text("$label:")
        Text(value)
    }
}

@Composable
internal fun VerticalAbilities(abilities: List<AbilityStats>, modifier: Modifier = Modifier) {
    Column(modifier.subtleBorder().padding(12.dp), verticalArrangement = Arrangement.spacedBy(32.dp)) {
        abilities.forEach { ability ->
            val savingThrowColor = if (ability.savingThrowProficient) Theme.colors.textPrimary else Theme.colors.textSecondary
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${ability.label} (${ability.score})",
                        color = Theme.colors.textPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        ability.score.abilityModifier().signed(),
                        style = Theme.typography.bodyLarge,
                        color = Theme.colors.textPrimary
                    )
                }
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(Res.string.saving_throws),
                        style = Theme.typography.bodySmall,
                        color = savingThrowColor,
                        modifier = Modifier.weight(1f)
                    )
                    Text(ability.savingThrow.signed(), color = savingThrowColor)
                }
                ability.skills.forEach { skill ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(skill.first, style = Theme.typography.bodySmall, modifier = Modifier.weight(1f))
                        Text(skill.second.signed())
                    }
                }
            }
        }
    }
}

@Composable
internal fun Modifier.subtleBorder() =
    border(1.dp, Theme.colors.textSecondary.copy(alpha = 0.15f), Theme.shapes.roundedSmall)
