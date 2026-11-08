package app.roleparty.application.backend.error

import app.roleparty.account.commons.AccountId
import app.roleparty.account.domain.model.AccountError
import app.roleparty.application.backend.i18n.Locales
import app.roleparty.campaign.agenda.commons.GameSessionId
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.commons.CampaignId
import app.roleparty.campaign.commons.PlayerCharacterId
import app.roleparty.campaign.story.commons.SceneId
import app.roleparty.campaign.creature.commons.MonsterSheetId
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.domain.model.playercharacter.classes.CharacterClass
import app.roleparty.campaign.library.commons.LibraryEntryId
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.campaign.story.domain.model.StoryError
import app.roleparty.communication.commons.CommunicationError
import app.roleparty.communication.commons.EmailAddress
import app.roleparty.media.commons.ImageFormat
import app.roleparty.media.domain.model.MediaError
import app.roleparty.security.domain.model.SecurityError
import io.ktor.http.HttpStatusCode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.util.Locale

class ErrorResponseTest {

    private val emailAddress = EmailAddress.from("player@roleparty.app")

    @Test
    fun `should translate an error with its parameters in English`() {
        // When
        val localized = AccountError.EmailAddressAlreadyUsed(emailAddress).localize(Locale.ENGLISH)

        // Then
        assertEquals(HttpStatusCode.Conflict, localized.status)
        assertEquals("account.email-address-already-used", localized.response.code)
        assertEquals("The email address player@roleparty.app is already used by another account.", localized.response.message)
    }

    @Test
    fun `should translate an error with its parameters in French`() {
        // When
        val localized = AccountError.EmailAddressAlreadyUsed(emailAddress).localize(Locale.FRENCH)

        // Then
        assertEquals("L’adresse e-mail player@roleparty.app est déjà utilisée par un autre compte.", localized.response.message)
    }

    @Test
    fun `should use English for a locale without translation, regardless of the JVM default locale`() {
        // Given
        val defaultLocale = Locale.getDefault()
        Locale.setDefault(Locale.FRENCH)

        try {
            // When
            val localized = AccountError.AdminRoleRequired.localize(Locale.GERMAN)

            // Then
            assertEquals("Only an administrator can perform this action.", localized.response.message)
        } finally {
            Locale.setDefault(defaultLocale)
        }
    }

    @TestFactory
    fun `should translate every error in every supported locale`() = allErrors.map { (val name, val translate) ->
        dynamicTest(name) {
            // When
            val messages = Locales.SUPPORTED.map { translate(it).response.message }

            // Then
            messages.forEach { assertTrue(it.isNotBlank() && "{" !in it, "Unresolved message: $it") }
            assertEquals(messages.size, messages.toSet().size, "Each locale should have its own translation")
        }
    }

    private val allErrors = listOf(
        ErrorCase("EmailAddressAlreadyUsed") { AccountError.EmailAddressAlreadyUsed(emailAddress).localize(it) },
        ErrorCase("AccountNotFound") { AccountError.AccountNotFound(AccountId.generate()).localize(it) },
        ErrorCase("AdminRoleRequired") { AccountError.AdminRoleRequired.localize(it) },
        ErrorCase("OwnRoleChangeForbidden") { AccountError.OwnRoleChangeForbidden.localize(it) },
        ErrorCase("InvalidPassword") { SecurityError.InvalidPassword.localize(it) },
        ErrorCase("InvalidCredentials") { SecurityError.InvalidCredentials.localize(it) },
        ErrorCase("InvalidRefreshToken") { SecurityError.InvalidRefreshToken.localize(it) },
        ErrorCase("InvalidEmailCode") { SecurityError.InvalidEmailCode.localize(it) },
        ErrorCase("TooManyAttempts") { SecurityError.TooManyAttempts.localize(it) },
        ErrorCase("InvalidEmailAddress") { CommunicationError.InvalidEmailAddress("not-an-email").localize(it) },
        ErrorCase("ImageTooLarge") { MediaError.ImageTooLarge(10 * 1024 * 1024).localize(it) },
        ErrorCase("InvalidImage") { MediaError.InvalidImage(ImageFormat.JPEG).localize(it) },
        ErrorCase("CampaignNotFound") { CampaignError.CampaignNotFound(CampaignId.generate()).localize(it) },
        ErrorCase("GameMasterRequired") { CampaignError.GameMasterRequired.localize(it) },
        ErrorCase("ParticipantRequired") { CampaignError.ParticipantRequired.localize(it) },
        ErrorCase("BlankName") { CampaignError.BlankName.localize(it) },
        ErrorCase("InvalidInvitationCode") { CampaignError.InvalidInvitationCode.localize(it) },
        ErrorCase("InvitationCodeAlreadyUsed") { CampaignError.InvitationCodeAlreadyUsed.localize(it) },
        ErrorCase("AlreadyParticipant") { CampaignError.AlreadyParticipant.localize(it) },
        ErrorCase("PlayerCharacterNotFound") { CampaignError.PlayerCharacterNotFound(PlayerCharacterId.generate()).localize(it) },
        ErrorCase("PlayerCharacterOwnerRequired") { CampaignError.PlayerCharacterOwnerRequired.localize(it) },
        ErrorCase("WrongSkillCount") { CampaignError.WrongSkillCount(2).localize(it) },
        ErrorCase("ClassNotHeld") { CampaignError.ClassNotHeld(CharacterClass.WIZARD).localize(it) },
        ErrorCase("MaxLevelReached") { CampaignError.MaxLevelReached(CharacterClass.WIZARD).localize(it) },
        ErrorCase("SceneNotFound") { StoryError.SceneNotFound(SceneId.generate()).localize(it) },
        ErrorCase("DateInThePast") { AgendaError.DateInThePast.localize(it) },
        ErrorCase("GameSessionNotFound") { AgendaError.GameSessionNotFound(GameSessionId.generate()).localize(it) },
        ErrorCase("BlankTitle") { LibraryError.BlankTitle.localize(it) },
        ErrorCase("LibraryEntryNotFound") { LibraryError.LibraryEntryNotFound(LibraryEntryId.generate()).localize(it) },
        ErrorCase("MonsterSheetNotFound") { CreatureError.MonsterSheetNotFound(MonsterSheetId.generate()).localize(it) },
    ) + CreatureError.InvalidMonsterSheet.Field.entries.map { field ->
        ErrorCase("InvalidMonsterSheet.$field") { CreatureError.InvalidMonsterSheet(field).localize(it) }
    }

    private data class ErrorCase(val name: String, val translate: (Locale) -> LocalizedError)
}
