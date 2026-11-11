package app.roleparty.campaign.creature.domain.service

import app.roleparty.account.commons.AccountId
import app.roleparty.campaign.creature.commons.PlayerCharacterId
import app.roleparty.campaign.creature.domain.model.CreatureError
import app.roleparty.campaign.creature.domain.model.playercharacter.Ability
import app.roleparty.campaign.creature.domain.model.playercharacter.CharacterClass
import app.roleparty.campaign.creature.domain.model.playercharacter.PlayerCharacter
import app.roleparty.campaign.creature.domain.model.playercharacter.PlayerCharacterCreation
import app.roleparty.campaign.creature.domain.port.primary.CreatePlayerCharacter
import app.roleparty.campaign.creature.domain.port.secondary.PlayerCharacters
import app.roleparty.core.commons.Outcome

private const val MIN_ABILITY_SCORE = 1
private const val MAX_ABILITY_SCORE = 20

internal class PlayerCharacterCreatingService(
    private val playerCharacters: PlayerCharacters,
    private val participant: ParticipantCheckingService,
) : CreatePlayerCharacter {

    override suspend fun invoke(player: AccountId, creation: PlayerCharacterCreation): Outcome<PlayerCharacterId, CreatureError> {
        participant.check(player, creation.campaignId)?.let { return Outcome.Failure(it) }
        validate(creation)?.let { return Outcome.Failure(it) }

        val playerCharacter = PlayerCharacter(
            id = PlayerCharacterId.generate(),
            campaignId = creation.campaignId,
            player = player,
            name = creation.name.trim(),
            image = creation.image,
            classes = listOf(CharacterClass(creation.className.trim(), 1, creation.hitDie)),
            classFeatures = creation.classFeatures,
            savingThrows = creation.savingThrows,
            armorTrainings = creation.armorTrainings,
            background = creation.background.copy(name = creation.background.name.trim()),
            skillProficiencies = creation.skillProficiencies,
            species = creation.species.copy(name = creation.species.name.trim()),
            languages = creation.languages.map { it.trim() }.filter { it.isNotEmpty() }.distinct(),
            equipment = creation.equipment,
            coins = creation.coins,
            abilityScores = creation.abilityScores,
            alignment = creation.alignment,
            details = creation.details,
        )
        playerCharacters.save(playerCharacter)
        return Outcome.Success(playerCharacter.id)
    }

    private fun validate(creation: PlayerCharacterCreation): CreatureError? = when {
        creation.name.isBlank() -> CreatureError.BlankName
        creation.className.isBlank() -> CreatureError.BlankClassName
        creation.background.name.isBlank() -> CreatureError.BlankBackgroundName
        creation.species.name.isBlank() -> CreatureError.BlankSpeciesName
        creation.species.speed < 0 || creation.coins < 0 -> CreatureError.NegativeValue
        else -> Ability.entries.firstOrNull { creation.abilityScores[it] !in MIN_ABILITY_SCORE..MAX_ABILITY_SCORE }
            ?.let { CreatureError.AbilityScoreOutOfRange(it, MIN_ABILITY_SCORE, MAX_ABILITY_SCORE) }
    }
}
