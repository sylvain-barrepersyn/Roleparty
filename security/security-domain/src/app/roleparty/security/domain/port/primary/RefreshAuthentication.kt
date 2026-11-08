package app.roleparty.security.domain.port.primary

import app.roleparty.core.commons.Outcome
import app.roleparty.security.domain.model.Authentication
import app.roleparty.security.domain.model.RefreshToken
import app.roleparty.security.domain.model.SecurityError

interface RefreshAuthentication {

    suspend operator fun invoke(refreshToken: RefreshToken): Outcome<Authentication, SecurityError>
}
