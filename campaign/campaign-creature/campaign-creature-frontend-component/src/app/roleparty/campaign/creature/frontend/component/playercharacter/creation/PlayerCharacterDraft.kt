package app.roleparty.campaign.creature.frontend.component.playercharacter.creation

import app.roleparty.campaign.creature.contract.web.AbilityName
import app.roleparty.campaign.creature.contract.web.AbilityScoresBody
import app.roleparty.campaign.creature.contract.web.AlignmentName
import app.roleparty.campaign.creature.contract.web.ArmorTrainingName
import app.roleparty.campaign.creature.contract.web.BackgroundBody
import app.roleparty.campaign.creature.contract.web.HitDieName
import app.roleparty.campaign.creature.contract.web.PlayerCharacterCreationRequestBody
import app.roleparty.campaign.creature.contract.web.PlayerCharacterDetailsBody
import app.roleparty.campaign.creature.contract.web.SizeName
import app.roleparty.campaign.creature.contract.web.SkillName
import app.roleparty.campaign.creature.contract.web.SpeciesBody
import app.roleparty.media.commons.ImageId

enum class PlayerCharacterCreationStep { CLASS, ORIGIN, ABILITY_SCORES, ALIGNMENT, DETAILS, NAME }

enum class AbilityScoreMethod { STANDARD_ARRAY, RANDOM, POINT_BUY }

enum class BackgroundBonus { PLUS_TWO_PLUS_ONE, PLUS_ONE_TO_THREE }

data class PlayerCharacterDraft(
    val className: String = "",
    val hitDie: HitDieName? = null,
    val savingThrows: Set<AbilityName> = emptySet(),
    val armorTrainings: Set<ArmorTrainingName> = emptySet(),
    val classSkills: Set<SkillName> = emptySet(),
    val backgroundName: String = "",
    val originFeat: String = "",
    val toolProficiency: String = "",
    val backgroundSkills: Set<SkillName> = emptySet(),
    val equipment: String = "",
    val coins: Int? = 0,
    val speciesName: String = "",
    val size: SizeName? = SizeName.MEDIUM,
    val speed: Int? = DEFAULT_SPEED,
    val speciesTraits: String = "",
    val backstory: String = "",
    val languages: String = DEFAULT_LANGUAGE,
    val scoreMethod: AbilityScoreMethod = AbilityScoreMethod.STANDARD_ARRAY,
    val rolledScores: List<Int> = emptyList(),
    val baseScores: Map<AbilityName, Int> = emptyMap(),
    val backgroundBonus: BackgroundBonus = BackgroundBonus.PLUS_TWO_PLUS_ONE,
    val plusTwo: AbilityName? = null,
    val plusOne: AbilityName? = null,
    val plusOnes: Set<AbilityName> = emptySet(),
    val appearance: String = "",
    val personality: String = "",
    val alignment: AlignmentName? = null,
    val classFeatures: String = "",
    val otherDetails: String = "",
    val name: String = "",
    val imageId: ImageId? = null,
) {

    val scorePool: List<Int>
        get() = when (scoreMethod) {
            AbilityScoreMethod.STANDARD_ARRAY -> STANDARD_ARRAY
            AbilityScoreMethod.RANDOM -> rolledScores
            AbilityScoreMethod.POINT_BUY -> emptyList()
        }

    val unassignedScores: List<Int>
        get() = baseScores.values.fold(scorePool.sortedDescending()) { remaining, score ->
            val index = remaining.indexOf(score)
            if (index < 0) remaining else remaining.filterIndexed { i, _ -> i != index }
        }

    val pointsSpent: Int get() = baseScores.values.sumOf { POINT_COSTS[it] ?: 0 }

    val skills: Set<SkillName> get() = classSkills + backgroundSkills

    fun bonus(ability: AbilityName): Int = when (backgroundBonus) {
        BackgroundBonus.PLUS_TWO_PLUS_ONE -> when (ability) {
            plusTwo -> 2
            plusOne -> 1
            else -> 0
        }
        BackgroundBonus.PLUS_ONE_TO_THREE -> if (ability in plusOnes) 1 else 0
    }

    fun score(ability: AbilityName): Int? = baseScores[ability]?.let { minOf(MAX_SCORE, it + bonus(ability)) }

    fun modifier(ability: AbilityName): Int? = score(ability)?.let { Math.floorDiv(it - 10, 2) }

    val maxHitPoints: Int? get() = hitDie?.let { die -> modifier(AbilityName.CONSTITUTION)?.let { maxOf(1, die.faces + it) } }

    fun isComplete(step: PlayerCharacterCreationStep): Boolean = when (step) {
        PlayerCharacterCreationStep.CLASS -> className.isNotBlank() && hitDie != null
        PlayerCharacterCreationStep.ORIGIN -> backgroundName.isNotBlank() && speciesName.isNotBlank() && size != null && speed != null && coins != null
        PlayerCharacterCreationStep.ABILITY_SCORES -> scoresAssigned() && bonusChosen()
        PlayerCharacterCreationStep.ALIGNMENT -> alignment != null
        PlayerCharacterCreationStep.DETAILS -> true
        PlayerCharacterCreationStep.NAME -> name.isNotBlank() && imageId != null
    }

    fun isReachable(step: PlayerCharacterCreationStep): Boolean =
        PlayerCharacterCreationStep.entries.takeWhile { it != step }.all(::isComplete)

    fun toRequestBody(): PlayerCharacterCreationRequestBody? {
        if (!PlayerCharacterCreationStep.entries.all(::isComplete)) return null
        return PlayerCharacterCreationRequestBody(
            name = name.trim(),
            imageId = imageId ?: return null,
            className = className.trim(),
            hitDie = hitDie ?: return null,
            classFeatures = classFeatures,
            savingThrows = savingThrows,
            armorTrainings = armorTrainings,
            background = BackgroundBody(backgroundName.trim(), originFeat.trim(), toolProficiency.trim()),
            skills = skills,
            species = SpeciesBody(speciesName.trim(), size ?: return null, speed ?: return null, speciesTraits),
            languages = languages.split(',').map { it.trim() }.filter { it.isNotEmpty() },
            equipment = equipment,
            coins = coins ?: return null,
            abilityScores = AbilityScoresBody(
                strength = score(AbilityName.STRENGTH) ?: return null,
                dexterity = score(AbilityName.DEXTERITY) ?: return null,
                constitution = score(AbilityName.CONSTITUTION) ?: return null,
                intelligence = score(AbilityName.INTELLIGENCE) ?: return null,
                wisdom = score(AbilityName.WISDOM) ?: return null,
                charisma = score(AbilityName.CHARISMA) ?: return null,
            ),
            alignment = alignment ?: return null,
            details = PlayerCharacterDetailsBody(appearance, personality, backstory, otherDetails),
        )
    }

    fun withScoreMethod(method: AbilityScoreMethod): PlayerCharacterDraft = copy(
        scoreMethod = method,
        baseScores = if (method == AbilityScoreMethod.POINT_BUY) AbilityName.entries.associateWith { POINT_BUY_MIN } else emptyMap(),
    )

    private fun scoresAssigned(): Boolean {
        if (!AbilityName.entries.all { it in baseScores }) return false
        return when (scoreMethod) {
            AbilityScoreMethod.POINT_BUY -> baseScores.values.all { it in POINT_BUY_MIN..POINT_BUY_MAX } && pointsSpent <= POINT_BUY_BUDGET
            else -> scorePool.size == AbilityName.entries.size && baseScores.values.sorted() == scorePool.sorted()
        }
    }

    private fun bonusChosen(): Boolean = when (backgroundBonus) {
        BackgroundBonus.PLUS_TWO_PLUS_ONE -> plusTwo != null && plusOne != null && plusTwo != plusOne
        BackgroundBonus.PLUS_ONE_TO_THREE -> plusOnes.size == 3
    }

    companion object {
        const val DEFAULT_SPEED = 30
        const val DEFAULT_LANGUAGE = "Common"
        const val MAX_SCORE = 20
        const val POINT_BUY_MIN = 8
        const val POINT_BUY_MAX = 15
        const val POINT_BUY_BUDGET = 27
        val STANDARD_ARRAY = listOf(15, 14, 13, 12, 10, 8)
        val POINT_COSTS = mapOf(8 to 0, 9 to 1, 10 to 2, 11 to 3, 12 to 4, 13 to 5, 14 to 7, 15 to 9)
    }
}
