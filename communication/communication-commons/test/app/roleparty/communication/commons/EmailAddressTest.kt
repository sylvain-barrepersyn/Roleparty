package app.roleparty.communication.commons

import app.roleparty.communication.commons.CommunicationError.InvalidEmailAddress
import app.roleparty.core.commons.Outcome
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EmailAddressTest {

    @Test
    fun `should parse a valid email address`() {
        // When
        val outcome = EmailAddress.parse("player@roleparty.app")

        // Then
        assertEquals(Outcome.Success(EmailAddress.from("player@roleparty.app")), outcome)
    }

    @Test
    fun `should fail to parse an invalid email address`() {
        // When
        val outcome = EmailAddress.parse("not-an-email")

        // Then
        assertEquals(Outcome.Failure(InvalidEmailAddress("not-an-email")), outcome)
    }

    @Test
    fun `should treat addresses differing only by case or surrounding spaces as the same`() {
        // When
        val parsed = EmailAddress.parse("  Gimli@RoleParty.App ")

        // Then
        assertEquals(Outcome.Success(EmailAddress.from("gimli@roleparty.app")), parsed)
        assertEquals(EmailAddress.from("gimli@roleparty.app"), EmailAddress.from("GIMLI@roleparty.app"))
    }
}
