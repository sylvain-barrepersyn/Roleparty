package app.roleparty.security.domain.port.primary

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome
import app.roleparty.security.domain.model.Authentication
import app.roleparty.security.domain.model.SecurityError

interface SignIn {

    suspend operator fun invoke(emailAddress: EmailAddress, password: String): Outcome<Authentication, SecurityError>
}
