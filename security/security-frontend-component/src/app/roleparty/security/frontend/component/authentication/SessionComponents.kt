package app.roleparty.security.frontend.component.authentication

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.roleparty.core.designsystem.button.Button
import app.roleparty.core.designsystem.icon.Icon
import app.roleparty.core.designsystem.icon.IconVector
import app.roleparty.core.designsystem.theme.Theme
import app.roleparty.security.frontend.component.resources.Res
import app.roleparty.security.frontend.component.resources.sign_out
import org.jetbrains.compose.resources.stringResource

@Composable
fun SessionRestoring() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Icon(vector = IconVector.ROLEPARTY, tint = Theme.colors.primary)
    }
}

@Composable
fun SignOutButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(leadingIcon = IconVector.CANCEL, text = stringResource(Res.string.sign_out), onClick = onClick, modifier = modifier)
}
