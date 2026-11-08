package app.roleparty.application.backend.error

import app.roleparty.account.domain.model.AccountError
import app.roleparty.application.backend.i18n.Messages
import app.roleparty.campaign.agenda.domain.model.AgendaError
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.domain.model.CampaignError
import app.roleparty.campaign.library.domain.model.LibraryError
import app.roleparty.campaign.story.domain.model.StoryError
import app.roleparty.communication.commons.CommunicationError
import app.roleparty.media.domain.model.MediaError
import app.roleparty.security.domain.model.SecurityError
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
data class ErrorResponse(val code: String, val message: String)

class LocalizedError(val status: HttpStatusCode, val response: ErrorResponse)

fun AccountError.localize(locale: Locale) = when (this) {
    is AccountError.EmailAddressAlreadyUsed ->
        localize(HttpStatusCode.Conflict, "account.email-address-already-used", locale, emailAddress)

    is AccountError.AccountNotFound ->
        localize(HttpStatusCode.NotFound, "account.not-found", locale, accountId)

    AccountError.AdminRoleRequired ->
        localize(HttpStatusCode.Forbidden, "account.admin-role-required", locale)

    AccountError.OwnRoleChangeForbidden ->
        localize(HttpStatusCode.Forbidden, "account.own-role-change-forbidden", locale)
}

fun SecurityError.localize(locale: Locale) = when (this) {
    SecurityError.InvalidPassword ->
        localize(HttpStatusCode.BadRequest, "security.invalid-password", locale)

    SecurityError.InvalidCredentials ->
        localize(HttpStatusCode.Unauthorized, "security.invalid-credentials", locale)

    SecurityError.InvalidRefreshToken ->
        localize(HttpStatusCode.Unauthorized, "security.invalid-refresh-token", locale)

    SecurityError.InvalidEmailCode ->
        localize(HttpStatusCode.BadRequest, "security.invalid-email-code", locale)

    SecurityError.TooManyAttempts ->
        localize(HttpStatusCode.TooManyRequests, "security.too-many-attempts", locale)
}

fun CampaignError.localize(locale: Locale) = when (this) {
    is CampaignError.CampaignNotFound -> localize(HttpStatusCode.NotFound, "campaign.not-found", locale, campaignId)
    CampaignError.GameMasterRequired -> localize(HttpStatusCode.Forbidden, "campaign.game-master-required", locale)
    CampaignError.ParticipantRequired -> localize(HttpStatusCode.Forbidden, "campaign.participant-required", locale)
    CampaignError.BlankName -> localize(HttpStatusCode.BadRequest, "campaign.blank-name", locale)
    CampaignError.InvalidInvitationCode -> localize(HttpStatusCode.BadRequest, "campaign.invalid-invitation-code", locale)
    CampaignError.InvitationCodeAlreadyUsed -> localize(HttpStatusCode.Conflict, "campaign.invitation-code-already-used", locale)
    CampaignError.AlreadyParticipant -> localize(HttpStatusCode.Conflict, "campaign.already-participant", locale)
    is CampaignError.PlayerCharacterNotFound -> localize(HttpStatusCode.NotFound, "campaign.player-character-not-found", locale, playerCharacterId)
    CampaignError.PlayerCharacterOwnerRequired -> localize(HttpStatusCode.Forbidden, "campaign.player-character-owner-required", locale)
    is CampaignError.WrongSkillCount -> localize(HttpStatusCode.BadRequest, "campaign.wrong-skill-count", locale, expected)
    is CampaignError.ClassNotHeld -> localize(HttpStatusCode.BadRequest, "campaign.class-not-held", locale, characterClass.name)
    is CampaignError.MaxLevelReached -> localize(HttpStatusCode.Conflict, "campaign.max-level-reached", locale, characterClass.name)
}

fun StoryError.localize(locale: Locale) = when (this) {
    is StoryError.CampaignNotFound -> localize(HttpStatusCode.NotFound, "campaign.not-found", locale, campaignId)
    StoryError.GameMasterRequired -> localize(HttpStatusCode.Forbidden, "campaign.game-master-required", locale)
    StoryError.BlankName -> localize(HttpStatusCode.BadRequest, "campaign.blank-name", locale)
    is StoryError.SceneNotFound -> localize(HttpStatusCode.NotFound, "campaign.story.scene-not-found", locale, sceneId)
    is StoryError.LibraryEntryNotFound -> localize(HttpStatusCode.NotFound, "campaign.library.entry-not-found", locale, libraryEntryId)
}

fun AgendaError.localize(locale: Locale) = when (this) {
    is AgendaError.CampaignNotFound -> localize(HttpStatusCode.NotFound, "campaign.not-found", locale, campaignId)
    AgendaError.GameMasterRequired -> localize(HttpStatusCode.Forbidden, "campaign.game-master-required", locale)
    AgendaError.ParticipantRequired -> localize(HttpStatusCode.Forbidden, "campaign.participant-required", locale)
    AgendaError.BlankName -> localize(HttpStatusCode.BadRequest, "campaign.blank-name", locale)
    AgendaError.DateInThePast -> localize(HttpStatusCode.BadRequest, "campaign.agenda.date-in-the-past", locale)
    is AgendaError.GameSessionNotFound -> localize(HttpStatusCode.NotFound, "campaign.agenda.game-session-not-found", locale, gameSessionId)
}

fun CreatureError.localize(locale: Locale) = when (this) {
    is CreatureError.CampaignNotFound -> localize(HttpStatusCode.NotFound, "campaign.not-found", locale, campaignId)
    CreatureError.GameMasterRequired -> localize(HttpStatusCode.Forbidden, "campaign.game-master-required", locale)
    is CreatureError.MonsterSheetNotFound -> localize(HttpStatusCode.NotFound, "campaign.creature.monster-sheet-not-found", locale, monsterSheetId)
    is CreatureError.InvalidMonsterSheet -> localize(HttpStatusCode.BadRequest, "campaign.creature.invalid-${field.name.lowercase().replace('_', '-')}", locale)
}

fun LibraryError.localize(locale: Locale) = when (this) {
    is LibraryError.CampaignNotFound -> localize(HttpStatusCode.NotFound, "campaign.not-found", locale, campaignId)
    LibraryError.GameMasterRequired -> localize(HttpStatusCode.Forbidden, "campaign.game-master-required", locale)
    LibraryError.BlankTitle -> localize(HttpStatusCode.BadRequest, "campaign.library.blank-title", locale)
    is LibraryError.LibraryEntryNotFound -> localize(HttpStatusCode.NotFound, "campaign.library.entry-not-found", locale, libraryEntryId)
}

fun MediaError.localize(locale: Locale) = when (this) {
    is MediaError.ImageTooLarge ->
        localize(HttpStatusCode.PayloadTooLarge, "media.image-too-large", locale, maxBytes / (1024 * 1024))

    is MediaError.InvalidImage ->
        localize(HttpStatusCode.BadRequest, "media.invalid-image", locale, format.name)
}

fun CommunicationError.localize(locale: Locale) = when (this) {
    is CommunicationError.InvalidEmailAddress ->
        localize(HttpStatusCode.BadRequest, "communication.invalid-email-address", locale, value)
}

private fun localize(status: HttpStatusCode, code: String, locale: Locale, vararg arguments: Any) =
    LocalizedError(status, ErrorResponse(code, Messages.ERRORS.get(code, locale, *arguments.map(Any::toString).toTypedArray())))
