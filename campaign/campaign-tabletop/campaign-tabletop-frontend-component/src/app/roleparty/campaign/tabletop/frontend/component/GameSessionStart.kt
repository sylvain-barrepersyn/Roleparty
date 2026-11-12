package app.roleparty.campaign.tabletop.frontend.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.roleparty.campaign.tabletop.contract.web.StartedGameSessionBody
import app.roleparty.campaign.tabletop.frontend.component.resources.Res
import app.roleparty.campaign.tabletop.frontend.component.resources.game_session_started
import app.roleparty.campaign.tabletop.frontend.component.resources.session_started
import app.roleparty.campaign.tabletop.frontend.component.resources.start_session
import app.roleparty.core.designsystem.button.Button
import app.roleparty.core.designsystem.field.date.formatted
import app.roleparty.core.designsystem.icon.IconVector
import org.jetbrains.compose.resources.stringResource

@Composable
fun StartGameSessionButton(started: Boolean, starting: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        leadingIcon = IconVector.PLAY,
        onClick = onClick,
        modifier = modifier,
        text = stringResource(if (started) Res.string.session_started else Res.string.start_session),
        enabled = !started && !starting,
    )
}

@Composable
fun gameSessionStartedMessage(gameSession: StartedGameSessionBody): String =
    stringResource(Res.string.game_session_started, gameSession.label())

internal fun StartedGameSessionBody.label(): String = name ?: date.formatted()
