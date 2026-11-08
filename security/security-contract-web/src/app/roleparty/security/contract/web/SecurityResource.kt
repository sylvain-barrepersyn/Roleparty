package app.roleparty.security.contract.web

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Serializable
@Resource("/v1")
class SecurityResource {

    @Serializable
    @Resource("sign-up")
    class SignUp(val parent: SecurityResource = SecurityResource())

    @Serializable
    @Resource("verify-email")
    class VerifyEmail(val parent: SecurityResource = SecurityResource())

    @Serializable
    @Resource("sign-in")
    class SignIn(val parent: SecurityResource = SecurityResource())

    @Serializable
    @Resource("password-reset")
    class PasswordReset(val parent: SecurityResource = SecurityResource()) {

        @Serializable
        @Resource("request")
        class Request(val parent: PasswordReset = PasswordReset())

        @Serializable
        @Resource("confirm")
        class Confirm(val parent: PasswordReset = PasswordReset())
    }

    @Serializable
    @Resource("password")
    class Password(val parent: SecurityResource = SecurityResource())

    @Serializable
    @Resource("refresh")
    class Refresh(val parent: SecurityResource = SecurityResource())

    @Serializable
    @Resource("sign-out")
    class SignOut(val parent: SecurityResource = SecurityResource())

    @Serializable
    @Resource("sign-out-everywhere")
    class SignOutEverywhere(val parent: SecurityResource = SecurityResource())
}
