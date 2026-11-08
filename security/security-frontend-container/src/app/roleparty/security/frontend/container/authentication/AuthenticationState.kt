package app.roleparty.security.frontend.container.authentication

import app.roleparty.security.frontend.component.authentication.AuthenticationFailure
import app.roleparty.security.frontend.component.authentication.AuthenticationForm
import app.roleparty.security.frontend.component.authentication.AuthenticationNotice
import app.roleparty.core.frontend.container.ViewModelState

sealed interface AuthenticationState : ViewModelState {

    data object Restoring : AuthenticationState

    data class SignedOut(
        val form: AuthenticationForm = AuthenticationForm.SignIn,
        val knownEmailAddress: String = "",
        val loading: Boolean = false,
        val failure: AuthenticationFailure? = null,
        val notice: AuthenticationNotice? = null,
    ) : AuthenticationState

    data object SignedIn : AuthenticationState
}
