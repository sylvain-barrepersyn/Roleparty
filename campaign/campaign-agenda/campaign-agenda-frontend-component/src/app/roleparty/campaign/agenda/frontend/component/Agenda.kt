package app.roleparty.campaign.agenda.frontend.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.contract.web.GameSessionResponseBody
import app.roleparty.campaign.agenda.frontend.component.resources.*
import app.roleparty.core.designsystem.button.Button
import app.roleparty.core.designsystem.feedback.Callout
import app.roleparty.core.designsystem.feedback.CalloutKind
import app.roleparty.core.designsystem.feedback.EmptyState
import app.roleparty.core.designsystem.field.date.DatePickerDialog
import app.roleparty.core.designsystem.field.date.formatted
import app.roleparty.core.designsystem.field.text.StringField
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.layout.CenteredBox
import app.roleparty.core.designsystem.layout.DetailPane
import app.roleparty.core.designsystem.layout.ListDetailLayout
import app.roleparty.core.designsystem.layout.ListItem
import app.roleparty.core.designsystem.layout.Thumbnail
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import kotlinx.datetime.*
import kotlinx.datetime.TimeZone
import org.jetbrains.compose.resources.stringResource
import java.time.format.TextStyle
import java.util.*
import kotlin.time.Clock

@Composable
fun Agenda(
    sessions: List<GameSessionResponseBody>,
    selected: GameSessionResponseBody?,
    scheduling: Boolean,
    error: String?,
    onSelect: (GameSessionId) -> Unit,
    onOpenScheduling: () -> Unit,
    onCancelScheduling: () -> Unit,
    onSchedule: (LocalDate) -> Unit,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }

    ListDetailLayout(
        modifier = modifier,
        listToolbar = { Button(onClick = onOpenScheduling, leadingIcon = IconVector.ADD) },
        list = {
            error?.let { Callout(text = it, kind = CalloutKind.ERROR, modifier = Modifier.fillMaxWidth()) }
            if (sessions.isEmpty()) EmptyState(stringResource(Res.string.no_sessions))
            sessions.forEach { session ->
                ListItem(
                    selected = session.id == selected?.id,
                    onClick = { onSelect(session.id) },
                    thumbnail = { DateThumbnail(session.date) },
                ) {
                    Text(text = session.name ?: session.date.formatted(), style = Theme.typography.title, maxLines = 1)
                }
            }
        },
        detail = {
            if (selected == null) {
                CenteredBox { Text(stringResource(Res.string.select_session)) }
            } else {
                DetailPane {
                    Text(text = selected.date.formatted(), style = Theme.typography.headline)
                    StringField(
                        value = selected.name.orEmpty(),
                        onValueChange = onNameChange,
                        label = stringResource(Res.string.session_name),
                        placeholder = stringResource(Res.string.session_name_placeholder, selected.date.formatted()),
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        },
    )

    DatePickerDialog(
        displayed = scheduling,
        onDismiss = onCancelScheduling,
        onSelect = onSchedule,
        initialMonth = today.yearMonth,
        minDate = today,
    )
}

@Composable
private fun DateThumbnail(date: LocalDate) {
    Thumbnail {
        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = date.day.toString(), style = Theme.typography.title, maxLines = 1)
            Text(
                text = java.time.Month.of(date.month.number).getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()).take(3),
                style = Theme.typography.bodySmall,
                maxLines = 1,
            )
        }
    }
}
