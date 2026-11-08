package app.roleparty.security.frontend.container.authentication

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.roleparty.core.frontend.container.Container
import app.roleparty.security.frontend.component.authentication.AuthenticationForm
import app.roleparty.security.frontend.component.authentication.AuthenticationFormActions
import app.roleparty.security.frontend.component.authentication.AuthenticationForms
import app.roleparty.security.frontend.component.authentication.SessionRestoring

class AuthenticationContainer(
    private val onSignedIn: () -> Unit = {},
    private val onSignedOut: () -> Unit = {},
    private val signedIn: @Composable (signOut: () -> Unit) -> Unit,
) : Container<AuthenticationState, AuthenticationAction, AuthenticationEvent, AuthenticationViewModel>(AuthenticationViewModel::class) {

    @Composable
    override fun Content(state: AuthenticationState, handle: (AuthenticationAction) -> Unit) {
        when (state) {
            AuthenticationState.Restoring -> SessionRestoring()
            AuthenticationState.SignedIn -> signedIn { handle(AuthenticationAction.SignOut) }
            is AuthenticationState.SignedOut -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                AuthenticationForms(
                    form = state.form,
                    knownEmailAddress = state.knownEmailAddress,
                    loading = state.loading,
                    notice = state.notice,
                    failure = state.failure,
                    actions = remember(handle) { FormActions(handle) },
                )
            }
        }
    }

    override fun onEvent(event: AuthenticationEvent) = when (event) {
        AuthenticationEvent.SignedIn -> onSignedIn()
        AuthenticationEvent.SignedOut -> onSignedOut()
    }

    private class FormActions(private val handle: (AuthenticationAction) -> Unit) : AuthenticationFormActions {

        override fun show(form: AuthenticationForm) = handle(AuthenticationAction.Show(form))

        override fun signIn(emailAddress: String, password: String) = handle(AuthenticationAction.SignIn(emailAddress, password))

        override fun signUp(emailAddress: String, password: String) = handle(AuthenticationAction.SignUp(emailAddress, password))

        override fun verifyEmailAddress(emailAddress: String, code: String) =
            handle(AuthenticationAction.VerifyEmailAddress(emailAddress, code))

        override fun requestPasswordReset(emailAddress: String) = handle(AuthenticationAction.RequestPasswordReset(emailAddress))

        override fun resetPassword(emailAddress: String, code: String, newPassword: String) =
            handle(AuthenticationAction.ResetPassword(emailAddress, code, newPassword))
    }
}
