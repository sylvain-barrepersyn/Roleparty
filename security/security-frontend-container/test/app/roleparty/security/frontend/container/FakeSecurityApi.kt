package app.roleparty.security.frontend.container

import app.roleparty.core.commons.Outcome
import app.roleparty.security.frontend.container.data.SecurityApi
import app.roleparty.security.frontend.container.data.SecurityFailure
import app.roleparty.security.frontend.container.data.SessionStore
import app.roleparty.security.frontend.container.data.StoredSession

internal class InMemorySessionStore(var session: StoredSession? = null) : SessionStore {

    override suspend fun load() = session

    override suspend fun save(session: StoredSession) {
        this.session = session
    }

    override suspend fun clear() {
        session = null
    }
}

internal class FakeSecurityApi : SecurityApi {

    val calls = mutableListOf<String>()
    var failure: SecurityFailure? = null
    val session = StoredSession("access", "refresh")

    private fun <T> respond(call: String, value: T): Outcome<T, SecurityFailure> {
        calls += call
        return failure?.let { Outcome.Failure(it) } ?: Outcome.Success(value)
    }

    override suspend fun signUp(emailAddress: String, password: String) = respond("signUp:$emailAddress:$password", Unit)

    override suspend fun verifyEmailAddress(emailAddress: String, code: String) = respond("verify:$emailAddress:$code", Unit)

    override suspend fun signIn(emailAddress: String, password: String) = respond("signIn:$emailAddress:$password", session)

    override suspend fun refresh(refreshToken: String) = respond("refresh:$refreshToken", session)

    override suspend fun signOut(refreshToken: String) = respond("signOut:$refreshToken", Unit)

    override suspend fun requestPasswordReset(emailAddress: String) = respond("requestReset:$emailAddress", Unit)

    override suspend fun resetPassword(emailAddress: String, code: String, newPassword: String) =
        respond("reset:$emailAddress:$code:$newPassword", Unit)
}
