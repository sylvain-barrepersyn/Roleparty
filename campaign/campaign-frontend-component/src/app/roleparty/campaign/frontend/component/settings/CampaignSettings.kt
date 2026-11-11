package app.roleparty.campaign.frontend.component.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.roleparty.campaign.contract.web.CampaignResponseBody
import app.roleparty.campaign.contract.web.InvitationResponseBody
import app.roleparty.campaign.frontend.component.ErrorCallout
import app.roleparty.campaign.frontend.component.resources.Res
import app.roleparty.campaign.frontend.component.resources.about
import app.roleparty.campaign.frontend.component.resources.copy
import app.roleparty.campaign.frontend.component.resources.generate_invitation
import app.roleparty.campaign.frontend.component.resources.invitations
import app.roleparty.campaign.frontend.component.resources.invitations_hint
import app.roleparty.campaign.frontend.component.resources.no_invitations
import app.roleparty.campaign.frontend.component.resources.used
import app.roleparty.core.designsystem.button.Button
import app.roleparty.core.designsystem.button.ButtonSize
import app.roleparty.core.designsystem.button.IconButton
import app.roleparty.core.designsystem.feedback.EmptyState
import app.roleparty.core.designsystem.feedback.Loader
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.layout.DetailPane
import app.roleparty.core.designsystem.layout.SectionHeader
import app.roleparty.core.designsystem.text.Text
import app.roleparty.core.designsystem.theme.Theme
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import org.jetbrains.compose.resources.stringResource

@Composable
fun CampaignSettings(
    campaign: CampaignResponseBody,
    invitations: List<InvitationResponseBody>?,
    generating: Boolean,
    error: String?,
    onGenerate: () -> Unit,
    modifier: Modifier = Modifier,
    otherSections: @Composable ColumnScope.() -> Unit = {},
) {
    DetailPane(modifier = modifier) {
        SectionHeader(title = stringResource(Res.string.about))
        Text(text = campaign.description, color = Theme.colors.textPrimary)

        SectionHeader(title = stringResource(Res.string.invitations)) {
            if (generating) Loader(size = 20.dp)
            Button(
                text = stringResource(Res.string.generate_invitation),
                onClick = onGenerate,
                leadingIcon = IconVector.ADD,
                enabled = !generating,
                size = ButtonSize.SMALL,
            )
        }
        Text(text = stringResource(Res.string.invitations_hint))
        ErrorCallout(error, Modifier.fillMaxWidth())
        when {
            invitations == null -> Loader()
            invitations.isEmpty() -> EmptyState(stringResource(Res.string.no_invitations))
            else -> Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                invitations.forEach { InvitationRow(it) }
            }
        }
        otherSections()
    }
}

@Composable
private fun InvitationRow(invitation: InvitationResponseBody) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = invitation.code,
            style = Theme.typography.title,
            color = if (invitation.used) Theme.colors.textSecondary else Theme.colors.textPrimary,
        )
        if (invitation.used) {
            Text(text = stringResource(Res.string.used))
        } else {
            IconButton(
                icon = IconVector.COPY,
                contentDescription = stringResource(Res.string.copy),
                onClick = { Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(invitation.code), null) },
                size = ButtonSize.SMALL,
            )
        }
    }
}
