package app.roleparty.security.domain.port.primary

import app.roleparty.communication.commons.EmailAddress
import app.roleparty.core.commons.Outcome
import app.roleparty.security.domain.model.SecurityError

interface VerifyEmailAddress {

    suspend operator fun invoke(emailAddress: EmailAddress, code: String): Outcome<Unit, SecurityError>
}
