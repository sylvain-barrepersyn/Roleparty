package app.roleparty.account.commons

import kotlinx.serialization.Serializable

@Serializable
enum class AccountRole {
    USER,
    ADMIN,
}
