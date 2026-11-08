package app.roleparty.campaign.frontend.component.creation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.frontend.component.ErrorCallout
import app.roleparty.campaign.frontend.component.resources.Res
import app.roleparty.campaign.frontend.component.resources.campaign_description
import app.roleparty.campaign.frontend.component.resources.campaign_image
import app.roleparty.campaign.frontend.component.resources.campaign_name
import app.roleparty.campaign.frontend.component.resources.cancel
import app.roleparty.campaign.frontend.component.resources.create_campaign
import app.roleparty.campaign.frontend.component.resources.creating
import app.roleparty.core.designsystem.button.Button
import app.roleparty.core.designsystem.field.text.StringField
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import org.jetbrains.compose.resources.stringResource

@Composable
fun CampaignCreationForm(
    name: String,
    description: String,
    submitting: Boolean,
    canSubmit: Boolean,
    error: String?,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    imageField: @Composable (label: String, modifier: Modifier) -> Unit,
) {
    Column(modifier = modifier.widthIn(max = 700.dp).padding(64.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(text = stringResource(Res.string.create_campaign), style = Theme.typography.headline)
        imageField(stringResource(Res.string.campaign_image), Modifier.fillMaxWidth())
        StringField(
            value = name,
            onValueChange = onNameChange,
            label = stringResource(Res.string.campaign_name),
            enabled = !submitting,
            required = true,
            modifier = Modifier.fillMaxWidth(),
        )
        StringField(
            value = description,
            onValueChange = onDescriptionChange,
            label = stringResource(Res.string.campaign_description),
            enabled = !submitting,
            singleLine = false,
            modifier = Modifier.fillMaxWidth(),
        )
        ErrorCallout(error, Modifier.fillMaxWidth())
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(text = stringResource(Res.string.cancel), onClick = onCancel, leadingIcon = IconVector.CANCEL, enabled = !submitting)
            Button(
                text = stringResource(if (submitting) Res.string.creating else Res.string.create_campaign),
                onClick = onSubmit,
                leadingIcon = IconVector.ADD,
                enabled = canSubmit && !submitting,
            )
        }
    }
}
