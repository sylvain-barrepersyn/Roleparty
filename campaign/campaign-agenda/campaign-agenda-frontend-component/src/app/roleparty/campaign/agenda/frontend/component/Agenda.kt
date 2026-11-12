package app.roleparty.campaign.agenda.frontend.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.RowScope
import androidx.compose.ui.semantics.Role
import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.contract.web.ParticipantBody
import app.roleparty.campaign.agenda.contract.web.PresenceName
import app.roleparty.campaign.agenda.contract.web.GameSessionResponseBody
import app.roleparty.campaign.agenda.frontend.component.resources.*
import app.roleparty.core.designsystem.button.Button
import app.roleparty.core.designsystem.feedback.Callout
import app.roleparty.core.designsystem.feedback.CalloutKind
import app.roleparty.core.designsystem.feedback.EmptyState
import app.roleparty.core.designsystem.field.date.DatePickerDialog
import app.roleparty.core.designsystem.field.date.formatted
import app.roleparty.core.designsystem.field.select.ChoiceColumn
import app.roleparty.core.designsystem.field.select.ChoiceTable
import app.roleparty.core.designsystem.field.select.FieldOptionRow
import app.roleparty.core.designsystem.field.text.StringField
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.layout.CenteredBox
import app.roleparty.core.designsystem.layout.DetailPane
import app.roleparty.core.designsystem.layout.ListDetailLayout
import app.roleparty.core.designsystem.layout.ListItem
import app.roleparty.core.designsystem.layout.SectionHeader
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
    onCancelScheduling: () -> Unit,
    onSchedule: (LocalDate) -> Unit,
    participants: List<ParticipantBody>,
    requester: AccountId,
    onPresenceChange: (AccountId, PresenceName) -> Unit,
    onDigitalGamingTableChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onOpenScheduling: (() -> Unit)? = null,
    onNameChange: ((String) -> Unit)? = null,
    sessionToolbar: (@Composable RowScope.(GameSessionResponseBody) -> Unit)? = null,
) {
    val today = remember { Clock.System.todayIn(TimeZone.currentSystemDefault()) }

    ListDetailLayout(
        modifier = modifier,
        listToolbar = onOpenScheduling?.let { { Button(onClick = it, leadingIcon = IconVector.ADD) } },
        list = {
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
                DetailPane(toolbar = sessionToolbar?.let { toolbar -> { toolbar(selected) } }) {
                    Text(text = selected.date.formatted(), style = Theme.typography.headline)
                    error?.let { Callout(text = it, kind = CalloutKind.ERROR, modifier = Modifier.fillMaxWidth()) }
                    if (onNameChange != null) {
                        StringField(
                            value = selected.name.orEmpty(),
                            onValueChange = onNameChange,
                            label = stringResource(Res.string.session_name),
                            placeholder = stringResource(Res.string.session_name_placeholder, selected.date.formatted()),
                            modifier = Modifier.fillMaxWidth(),
                        )
                    } else {
                        selected.name?.let { Text(text = it, style = Theme.typography.title) }
                    }
                    Presences(selected, past = selected.date < today, participants, requester, onPresenceChange, onDigitalGamingTableChange)
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
private fun Presences(
    session: GameSessionResponseBody,
    past: Boolean,
    participants: List<ParticipantBody>,
    requester: AccountId,
    onPresenceChange: (AccountId, PresenceName) -> Unit,
    onDigitalGamingTableChange: (Boolean) -> Unit,
) {
    val requesterIsGameMaster = participants.any { it.gameMaster && it.accountId == requester }

    SectionHeader(stringResource(Res.string.presences))
    if (past) Text(text = stringResource(Res.string.session_over), style = Theme.typography.bodySmall, color = Theme.colors.textSecondary)
    ChoiceTable(
        rows = participants,
        columns = PresenceName.entries.map { ChoiceColumn(it, it.label()) },
        rowLabel = { ParticipantLabel(it, isRequester = it.accountId == requester) },
        selected = { participant -> session.presences.firstOrNull { it.accountId == participant.accountId }?.presence },
        onSelect = { participant, presence -> onPresenceChange(participant.accountId, presence) },
        enabled = { !past && (requesterIsGameMaster || it.accountId == requester) },
        available = { participant, presence -> !(participant.gameMaster && presence == PresenceName.ABSENT) },
        modifier = Modifier.fillMaxWidth(),
    )
    FieldOptionRow(
        label = stringResource(Res.string.digital_gaming_table),
        selected = session.digitalGamingTable,
        onClick = { onDigitalGamingTableChange(!session.digitalGamingTable) },
        enabled = requesterIsGameMaster && !past,
        role = Role.Checkbox,
    )
    Text(text = stringResource(Res.string.digital_gaming_table_hint), style = Theme.typography.bodySmall, color = Theme.colors.textSecondary)
}

@Composable
private fun RowScope.ParticipantLabel(participant: ParticipantBody, isRequester: Boolean) {
    val name = when {
        participant.gameMaster -> stringResource(Res.string.game_master)
        participant.characterNames.isEmpty() -> stringResource(Res.string.player_without_character)
        else -> participant.characterNames.joinToString()
    }
    Text(text = name, color = Theme.colors.textPrimary, maxLines = 1)
    if (isRequester) Text(text = stringResource(Res.string.you), style = Theme.typography.bodySmall, color = Theme.colors.textSecondary, maxLines = 1)
}

@Composable
internal fun PresenceName.label(): String = stringResource(
    when (this) {
        PresenceName.ABSENT -> Res.string.presence_absent
        PresenceName.IN_PERSON_WITH_COMPUTER -> Res.string.presence_in_person_with_computer
        PresenceName.IN_PERSON_WITHOUT_COMPUTER -> Res.string.presence_in_person_without_computer
        PresenceName.REMOTE -> Res.string.presence_remote
    }
)

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
