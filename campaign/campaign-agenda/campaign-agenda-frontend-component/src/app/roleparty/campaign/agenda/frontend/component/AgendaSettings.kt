package app.roleparty.campaign.agenda.frontend.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.agenda.contract.web.PresenceName
import app.roleparty.campaign.agenda.frontend.component.resources.*
import app.roleparty.core.designsystem.feedback.Callout
import app.roleparty.core.designsystem.feedback.CalloutKind
import app.roleparty.core.designsystem.field.select.FieldOptionRow
import app.roleparty.core.designsystem.layout.SectionHeader
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import org.jetbrains.compose.resources.stringResource

@Composable
fun AgendaSettings(
    gameMaster: Boolean,
    presence: PresenceName,
    digitalGamingTable: Boolean,
    error: String?,
    onPresenceChange: (PresenceName) -> Unit,
    onDigitalGamingTableChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        SectionHeader(stringResource(Res.string.agenda_settings))
        Text(text = stringResource(Res.string.agenda_settings_hint), style = Theme.typography.bodySmall, color = Theme.colors.textSecondary)
        error?.let { Callout(text = it, kind = CalloutKind.ERROR, modifier = Modifier.fillMaxWidth()) }
        Column {
            Text(text = stringResource(Res.string.default_presence), style = Theme.typography.title)
            PresenceName.entries.filter { !gameMaster || it != PresenceName.ABSENT }.forEach { option ->
                FieldOptionRow(label = option.label(), selected = presence == option, onClick = { onPresenceChange(option) })
            }
        }
        if (gameMaster) {
            FieldOptionRow(
                label = stringResource(Res.string.digital_gaming_table),
                selected = digitalGamingTable,
                onClick = { onDigitalGamingTableChange(!digitalGamingTable) },
                role = Role.Checkbox,
            )
            Text(text = stringResource(Res.string.digital_gaming_table_hint), style = Theme.typography.bodySmall, color = Theme.colors.textSecondary)
        }
    }
}
