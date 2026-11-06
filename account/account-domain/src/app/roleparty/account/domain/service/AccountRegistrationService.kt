package app.roleparty.account.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.account.domain.model.AccountCreationCommand.Register
import app.roleparty.account.domain.model.AccountDecider
import app.roleparty.account.domain.model.AccountError
import app.roleparty.account.domain.model.AccountError.EmailAddressAlreadyUsed
import app.roleparty.account.domain.model.AccountVersion
import app.roleparty.account.domain.port.primary.AccountOverviewQuery
import app.roleparty.account.domain.port.primary.AccountOverviewQuery.Filter
import app.roleparty.account.domain.port.primary.AccountOverviewQueryHandler
import app.roleparty.account.domain.port.primary.AccountRegistrationCommand
import app.roleparty.account.domain.port.primary.AccountRegistrationCommandHandler
import app.roleparty.account.domain.port.secondary.AccountAppendFailure
import app.roleparty.account.domain.port.secondary.Accounts
import app.roleparty.core.commons.Outcome
import app.roleparty.core.commons.getOrElse
import kotlin.time.Clock

internal class AccountRegistrationService(
    private val accounts: Accounts,
    private val accountOverviewQueryHandler: AccountOverviewQueryHandler,
    private val clock: Clock,
) : AccountRegistrationCommandHandler {

    override suspend fun handle(command: AccountRegistrationCommand): Outcome<AccountId, AccountError> {
        val existingAccount = accountOverviewQueryHandler.handle(
            query = AccountOverviewQuery(filter = Filter.ByEmailAddress(command.emailAddress))
        )
        if (existingAccount != null) return Outcome.Failure(EmailAddressAlreadyUsed(command.emailAddress))

        val accountId = AccountId.generate()
        val payloads = AccountDecider.decide(Register(emailAddress = command.emailAddress))
        accounts.append(accountId, expectedVersion = AccountVersion.INITIAL, events = payloads.stamp(accountId, clock))
            .getOrElse { failure ->
                when (failure) {
                    AccountAppendFailure.VersionConflict -> error("Account $accountId was just generated but its stream already exists")
                    is AccountAppendFailure.EmailAddressAlreadyUsed -> return Outcome.Failure(EmailAddressAlreadyUsed(failure.emailAddress))
                }
            }
        return Outcome.Success(accountId)
    }
}
