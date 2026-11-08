package app.roleparty.campaign.frontend.component.joining

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.frontend.component.ErrorCallout
import app.roleparty.campaign.frontend.component.resources.Res
import app.roleparty.campaign.frontend.component.resources.cancel
import app.roleparty.campaign.frontend.component.resources.invitation_code
import app.roleparty.campaign.frontend.component.resources.invitation_code_hint
import app.roleparty.campaign.frontend.component.resources.join_campaign
import app.roleparty.campaign.frontend.component.resources.joining
import app.roleparty.core.designsystem.button.Button
import app.roleparty.core.designsystem.field.text.StringField
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import org.jetbrains.compose.resources.stringResource

@Composable
fun CampaignJoiningForm(
    code: String,
    submitting: Boolean,
    canSubmit: Boolean,
    error: String?,
    onCodeChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.widthIn(max = 700.dp).padding(64.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = stringResource(Res.string.join_campaign), style = Theme.typography.headline)
        Text(text = stringResource(Res.string.invitation_code_hint))
        StringField(
            value = code,
            onValueChange = onCodeChange,
            label = stringResource(Res.string.invitation_code),
            placeholder = "ABC-DEF-GHJ-KLM",
            enabled = !submitting,
            required = true,
            keyboardActions = KeyboardActions(onDone = { if (canSubmit) onSubmit() }),
            modifier = Modifier.fillMaxWidth(),
        )
        ErrorCallout(error, Modifier.fillMaxWidth())
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(text = stringResource(Res.string.cancel), onClick = onCancel, leadingIcon = IconVector.CANCEL, enabled = !submitting)
            Button(
                text = stringResource(if (submitting) Res.string.joining else Res.string.join_campaign),
                onClick = onSubmit,
                leadingIcon = IconVector.NEXT,
                enabled = canSubmit && !submitting,
            )
        }
    }
}
