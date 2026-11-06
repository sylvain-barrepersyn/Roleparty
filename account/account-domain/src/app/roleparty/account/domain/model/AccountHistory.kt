package app.roleparty.account.domain.model

data class AccountHistory(val events: List<AccountEvent>, val version: AccountVersion)
