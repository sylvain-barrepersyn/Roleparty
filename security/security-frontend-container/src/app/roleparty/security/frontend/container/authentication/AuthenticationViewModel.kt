package app.roleparty.security.frontend.container.authentication

import androidx.lifecycle.viewModelScope
import app.roleparty.core.commons.Outcome
import app.roleparty.core.frontend.container.ViewModel
import app.roleparty.security.frontend.component.authentication.AuthenticationFailure
import app.roleparty.security.frontend.component.authentication.AuthenticationForm
import app.roleparty.security.frontend.component.authentication.AuthenticationNotice
import app.roleparty.security.frontend.container.authentication.AuthenticationState.Restoring
import app.roleparty.security.frontend.container.authentication.AuthenticationState.SignedIn
import app.roleparty.security.frontend.container.authentication.AuthenticationState.SignedOut
import app.roleparty.security.frontend.container.data.SecurityApi
import app.roleparty.security.frontend.container.data.SecurityFailure
import app.roleparty.security.frontend.container.data.SessionStore
import app.roleparty.security.frontend.container.data.StoredSession
import kotlinx.coroutines.launch

class AuthenticationViewModel(
    private val api: SecurityApi,
    private val store: SessionStore,
) : ViewModel<AuthenticationState, AuthenticationAction, AuthenticationEvent>(Restoring) {

    init {
        viewModelScope.launch { restore() }
    }

    override fun handle(action: AuthenticationAction) {
        when (action) {
            is AuthenticationAction.Show -> show(action.form)
            is AuthenticationAction.SignIn -> submit(knownEmailAddress = action.emailAddress) {
                onSuccess(api.signIn(action.emailAddress, action.password)) { signedIn(it) }
            }
            is AuthenticationAction.SignUp -> submit(knownEmailAddress = action.emailAddress) {
                onSuccess(api.signUp(action.emailAddress, action.password)) { show(AuthenticationForm.VerifyEmailAddress(action.emailAddress)) }
            }
            is AuthenticationAction.VerifyEmailAddress -> submit {
                onSuccess(api.verifyEmailAddress(action.emailAddress, action.code)) { backToSignIn(AuthenticationNotice.EMAIL_VERIFIED) }
            }
            is AuthenticationAction.RequestPasswordReset -> submit(knownEmailAddress = action.emailAddress) {
                onSuccess(api.requestPasswordReset(action.emailAddress)) { show(AuthenticationForm.ResetPassword(action.emailAddress)) }
            }
            is AuthenticationAction.ResetPassword -> submit {
                onSuccess(api.resetPassword(action.emailAddress, action.code, action.newPassword)) { backToSignIn(AuthenticationNotice.PASSWORD_RESET) }
            }
            AuthenticationAction.SignOut -> viewModelScope.launch { signOut() }
        }
    }

    private suspend fun restore() {
        val session = store.load() ?: return update { SignedOut() }

        when (val outcome = api.refresh(session.refreshToken)) {
            is Outcome.Success -> signedIn(outcome.value)
            is Outcome.Failure -> {
                if (outcome.error is SecurityFailure.Rejected) store.clear()
                update { SignedOut(failure = outcome.error.takeUnless { it is SecurityFailure.Rejected }?.toAuthenticationFailure()) }
            }
        }
    }

    private suspend fun signOut() {
        store.load()?.let { api.signOut(it.refreshToken) }
        store.clear()
        update { SignedOut() }
        emit(AuthenticationEvent.SignedOut)
    }

    private fun show(form: AuthenticationForm) = signedOut { it.copy(form = form, failure = null, notice = null) }

    private fun backToSignIn(notice: AuthenticationNotice) = signedOut { it.copy(form = AuthenticationForm.SignIn, notice = notice) }

    private suspend fun signedIn(session: StoredSession) {
        store.save(session)
        update { SignedIn }
        emit(AuthenticationEvent.SignedIn)
    }

    private fun submit(knownEmailAddress: String? = null, request: suspend () -> Unit) {
        val current = state.value as? SignedOut ?: return
        if (current.loading) return
        update { current.copy(knownEmailAddress = knownEmailAddress ?: current.knownEmailAddress, loading = true, failure = null, notice = null) }
        viewModelScope.launch {
            try {
                request()
            } finally {
                signedOut { it.copy(loading = false) }
            }
        }
    }

    private suspend fun <T> onSuccess(outcome: Outcome<T, SecurityFailure>, then: suspend (T) -> Unit) {
        when (outcome) {
            is Outcome.Success -> then(outcome.value)
            is Outcome.Failure -> signedOut { it.copy(failure = outcome.error.toAuthenticationFailure()) }
        }
    }

    private fun signedOut(transform: (SignedOut) -> SignedOut) = update { if (it is SignedOut) transform(it) else it }

    private fun SecurityFailure.toAuthenticationFailure(): AuthenticationFailure = when (this) {
        is SecurityFailure.Rejected -> AuthenticationFailure.Rejected(message)
        SecurityFailure.Unreachable -> AuthenticationFailure.Unreachable
        SecurityFailure.Unexpected -> AuthenticationFailure.Unexpected
    }
}
