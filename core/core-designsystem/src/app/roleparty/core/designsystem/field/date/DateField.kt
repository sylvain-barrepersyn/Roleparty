package app.roleparty.core.designsystem.field.date

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.roleparty.core.designsystem.button.ButtonSize
import app.roleparty.core.designsystem.button.IconButton
import app.roleparty.core.designsystem.dialog.Dialog
import app.roleparty.core.designsystem.surface.Surface
import app.roleparty.core.designsystem.theme.disabledIf
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.semantics.Role
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.designsystem.resources.Res
import app.roleparty.designsystem.resources.next_month
import app.roleparty.designsystem.resources.previous_month
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.YearMonth
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toJavaLocalDate
import kotlinx.datetime.yearMonth
import org.jetbrains.compose.resources.stringResource

fun LocalDate.formatted(): String = toJavaLocalDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(Locale.getDefault()))

@Composable
fun DatePickerDialog(
    displayed: Boolean,
    onDismiss: () -> Unit,
    onSelect: (LocalDate) -> Unit,
    initialMonth: YearMonth,
    minDate: LocalDate? = null,
) {
    Dialog(displayed = displayed, onDismiss = onDismiss, modifier = Modifier.width(360.dp)) {
        var month by remember { mutableStateOf(initialMonth) }

        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    icon = IconVector.PREVIOUS,
                    contentDescription = stringResource(Res.string.previous_month),
                    onClick = { month = month.minus(1, DateTimeUnit.MONTH) },
                    size = ButtonSize.SMALL,
                )
                Text(
                    text = "${java.time.Month.of(month.month.number).getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())} ${month.year}",
                    style = Theme.typography.title,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                )
                IconButton(
                    icon = IconVector.NEXT,
                    contentDescription = stringResource(Res.string.next_month),
                    onClick = { month = month.plus(1, DateTimeUnit.MONTH) },
                    size = ButtonSize.SMALL,
                )
            }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround, verticalAlignment = Alignment.CenterVertically) {
                DayOfWeek.entries.forEach {
                    Text(
                        text = java.time.DayOfWeek.of(it.isoDayNumber).getDisplayName(TextStyle.SHORT_STANDALONE, Locale.getDefault()),
                        style = Theme.typography.bodySmall
                    )
                }
            }

            monthWeeks(month).forEach { week ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    week.forEach { date ->
                        Box(Modifier.weight(1f).aspectRatio(1f), contentAlignment = Alignment.Center) {
                            if (date != null) {
                                DayCell(day = date.day, enabled = minDate == null || date >= minDate, onClick = { onSelect(date) })
                            } else {
                                Spacer(Modifier)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(day: Int, enabled: Boolean, onClick: () -> Unit) {
    Surface(onClick = onClick, enabled = enabled, role = Role.Button, modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = day.toString(), color = Theme.colors.textPrimary.disabledIf(!enabled))
    }
}

private fun monthWeeks(month: YearMonth): List<List<LocalDate?>> {
    val first = LocalDate(month.year, month.month, 1)
    val days = generateSequence(first) { it.plus(1, DateTimeUnit.DAY) }.takeWhile { it.yearMonth == month }.toList()
    val leading = List<LocalDate?>(first.dayOfWeek.isoDayNumber - 1) { null }
    return (leading + days).chunked(7).map { it + List(7 - it.size) { null } }
}
